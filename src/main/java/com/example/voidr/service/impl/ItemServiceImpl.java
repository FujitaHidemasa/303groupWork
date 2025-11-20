package com.example.voidr.service.impl;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.example.voidr.common.FilePath;
import com.example.voidr.entity.Item;
import com.example.voidr.helper.ItemHelper;
import com.example.voidr.helper.XmlHelper;
import com.example.voidr.repository.ItemCategoryMapper;
import com.example.voidr.repository.ItemImageMapper;
import com.example.voidr.repository.ItemMapper;
import com.example.voidr.service.ItemService;
import com.example.voidr.xml.ItemXmlInfo;
import com.example.voidr.xml.ItemXmlRoot;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService
{

	private final ItemMapper itemMapper;
	private final ItemCategoryMapper categoryMapper;
	private final ItemImageMapper imageMapper;

	@PostConstruct
	@Override
	public void syncItems()
	{
		/*
		 * itemMapper.countAll();
		 * spring.sql.init.mode=alwaysを切る場合は必須
		 * item テーブルに 1 件でもデータがあれば、XML → DB 同期は行わない
		 */
		int itemCount = itemMapper.countAll();
		if (itemCount > 0) {
			return;
		}

		try
		{
			File xmlFile = FilePath.ITEM_LIST_XML.getPathFile();
			if(!xmlFile.exists())
			{
				throw new Exception("ItemList.xmlが存在しません:" + FilePath.ITEM_LIST_XML.getRelativePath());
			}
			ItemXmlRoot xmlData = XmlHelper.loadXmlClass(xmlFile, ItemXmlRoot.class);
			List<ItemXmlInfo> xmlItems = xmlData.getItems();
			List<Item> dbItems = getAllItems();

			// DBのアイテムリストを「ID → Item」形式のMapに変換
			Map<Long, Item> dbMap = dbItems.stream()
			        .collect(Collectors.toMap(Item::getId, i -> i));

			// XMLのアイテムリストを「ID → ItemXmlInfo」形式のMapに変換
			Map<Long, ItemXmlInfo> xmlMap = xmlItems.stream()
			        .collect(Collectors.toMap(ItemXmlInfo::getId, i -> i));
			
			// XMLの更新時間を取得
			long lastModifiedMillis = xmlFile.lastModified(); // ミリ秒単位で取得
			LocalDateTime lastModifiedTime = LocalDateTime.ofInstant(
			        Instant.ofEpochMilli(lastModifiedMillis),
			        ZoneId.systemDefault()
			        );


			// INSERT or UPDATE
			for (ItemXmlInfo xmlItem : xmlItems)
			{
				Item dbItem = dbMap.get(xmlItem.getId());
				if(dbItem == null)
				{
					// INSERT
					Item newItem = ItemHelper.convertItem(xmlItem);
					newItem.setCreatedAt(lastModifiedTime);
					newItem.setUpdatedAt(lastModifiedTime);
					itemMapper.insert(newItem);
					insertCategories(xmlItem);
					insertImages(xmlItem);
				}
				else if(!isSame(dbItem, xmlItem))
				{
					// UPDATE
					dbItem.setName(xmlItem.getName());
					dbItem.setPrice(xmlItem.getPrice());
					dbItem.setOverview(xmlItem.getOverview());
					dbItem.setThumbsImageName(xmlItem.getThumbsImageName());
					dbItem.setUpdatedAt(lastModifiedTime);
					itemMapper.update(dbItem);

					// カテゴリ更新
					categoryMapper.deleteByItemId(dbItem.getId());
					insertCategories(xmlItem);

					// 画像更新
					imageMapper.deleteByItemId(dbItem.getId());
					insertImages(xmlItem);
				}
			}

			// DELETE（XMLに存在しない商品をDB側から退役させる）
			for (Item dbItem : dbItems)
			{
				if(!xmlMap.containsKey(dbItem.getId()))
				{
					// カテゴリ・画像は物理削除（カタログ用なのでOK）
					//categoryMapper.deleteByItemId(dbItem.getId());
					//imageMapper.deleteByItemId(dbItem.getId());
					
					// ★修正：物理削除 → ソフトデリート
					itemMapper.softDelete(dbItem.getId());
				}
			}

			System.out.println("✅ XML ↔ DB 同期完了");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void insertCategories(ItemXmlInfo xmlItem)
	{
		List<String> list = xmlItem.getCategoryList();
		if(list != null && !list.isEmpty())
		{
			for (String c : list)
			{
				categoryMapper.insert(xmlItem.getId(), c);
			}
		}
	}

	private void insertImages(ItemXmlInfo xmlItem)
	{
		List<String> list = xmlItem.getImagesName();
		if(list != null && !list.isEmpty())
		{
			for (String image : list)
			{
				imageMapper.insert(xmlItem.getId(), image);
			}
		}
	}

	private boolean isSame(Item dbItem, ItemXmlInfo xmlItem)
	{
		boolean basicMatch = Objects.equals(dbItem.getName(), xmlItem.getName())
				&& dbItem.getPrice() == xmlItem.getPrice()
				&& Objects.equals(dbItem.getOverview(), xmlItem.getOverview())
				&& Objects.equals(dbItem.getThumbsImageName(), xmlItem.getThumbsImageName());

		if(!basicMatch)
		{
			return false;
		}

		// カテゴリ比較
		List<String> dbCategories = categoryMapper.findByItemId(dbItem.getId());
		List<String> xmlCategories = xmlItem.getCategoryList() != null ? xmlItem.getCategoryList() : List.of();
		if(!listEqualsIgnoreOrder(dbCategories, xmlCategories))
			return false;

		// 画像比較
		List<String> dbImages = imageMapper.findByItemId(dbItem.getId());
		List<String> xmlImages = xmlItem.getImagesName() != null ? xmlItem.getCategoryList() : List.of();
		return listEqualsIgnoreOrder(dbImages, xmlImages);
	}

	private boolean listEqualsIgnoreOrder(List<String> a, List<String> b)
	{
		if(a.size() != b.size())
			return false;
		List<String> aSorted = new ArrayList<>(a);
		List<String> bSorted = new ArrayList<>(b);
		Collections.sort(aSorted);
		Collections.sort(bSorted);
		return aSorted.equals(bSorted);
	}

	@Override
	public List<Item> getAllItems()
	{
		List<Item> dbItems = itemMapper.selectAll();
		for (Item item : dbItems)
		{
			item.setCategoryList(categoryMapper.findByItemId(item.getId()));
			item.setImagesName(imageMapper.findByItemId(item.getId()));
		}
		return dbItems;
	}
	
	@Override
	public List<Item> getAllItemsIncludingDeleted() {

	    List<Item> dbItems = itemMapper.selectAllIncludingDeleted();

	    // カテゴリと画像名を紐付け（削除済みでも必要）
	    for (Item item : dbItems) {
	        item.setCategoryList(categoryMapper.findByItemId(item.getId()));
	        item.setImagesName(imageMapper.findByItemId(item.getId()));
	    }

	    return dbItems;
	}
	
	@Override
	public List<Item> searchItemsByKeyword(String keyword)
	{
	    List<Item> dbItems = itemMapper.selectByKeyword(keyword);
	    for (Item item : dbItems)
	    {
	        item.setCategoryList(categoryMapper.findByItemId(item.getId()));
	        item.setImagesName(imageMapper.findByItemId(item.getId()));
	    }
	    return dbItems;
	}

	@Override
	public List<Item> getItemsByRangeId(Integer min, Integer max)
	{
		return itemMapper.selectByRangeId(min, max);
	}

	@Override
	public List<Item> getItemsByCategory(String category)
	{
		return itemMapper.selectByCategory(category);
	}

	@Override
	public Item getItemById(Long id)
	{
		Item item = itemMapper.selectById(id);
		item.setCategoryList(categoryMapper.findByItemId(id));
		item.setImagesName(imageMapper.findByItemId(id));
		return item;
	}

	@Override
	public void createItem(Item item) 
	{
		// ★修正：管理画面からの新規登録は、DBのSERIALでIDを採番
		itemMapper.insertAuto(item);

		// ID 採番後、item.id に値が入るので、それを使って紐付け登録
		for (String c : item.getCategoryList()) {
			categoryMapper.insert(item.getId(), c);
		}
		for (String img : item.getImagesName()) {
			imageMapper.insert(item.getId(), img);
		}
	}


	@Override
	public void updateItem(Item item)
	{
		itemMapper.update(item);
		categoryMapper.deleteByItemId(item.getId());
		imageMapper.deleteByItemId(item.getId());
		for (String c : item.getCategoryList())
			categoryMapper.insert(item.getId(), c);
		for (String img : item.getImagesName())
			imageMapper.insert(item.getId(), img);
	}

	@Override
	public void deleteItem(Long id)
	{
		// カテゴリや画像は削除しない（管理画面で表示したいので）
		// categoryMapper.deleteByItemId(id);
		// imageMapper.deleteByItemId(id);
		itemMapper.softDelete(id);
	}
	
	@Override
	public void restoreItem(Long id) {
	    itemMapper.restoreItem(id);
	}
	
	@Override
	public List<Item> getLatestItems() {
	    return itemMapper.findLatestItems();
	}
	
	@Override
	public List<Item> getRandom4Items() {
	    List<Item> dbItems = itemMapper.selectRandom4();

	    for (Item item : dbItems) {
	        item.setCategoryList(categoryMapper.findByItemId(item.getId()));
	        item.setImagesName(imageMapper.findByItemId(item.getId()));
	    }

	    return dbItems;
	}

}
