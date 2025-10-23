package com.example.voidr.service.impl;

import java.time.LocalDateTime;
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
		try
		{
			if(!FilePath.ITEM_LIST_XML.exists())
			{
				throw new Exception("ItemList.xmlが存在しません:" + FilePath.ITEM_LIST_XML.getRelativePath());
			}
			ItemXmlRoot xmlData = XmlHelper.loadXmlClass(FilePath.ITEM_LIST_XML.getRelativePath(), ItemXmlRoot.class);
			List<ItemXmlInfo> xmlItems = xmlData.getItems();
			List<Item> dbItems = getAllItems();

			Map<Long, Item> dbMap = dbItems.stream()
					.collect(Collectors.toMap(Item::getId, i -> i));
			Map<Long, ItemXmlInfo> xmlMap = xmlItems.stream()
					.collect(Collectors.toMap(ItemXmlInfo::getId, i -> i));

			// INSERT or UPDATE
			for (ItemXmlInfo xmlItem : xmlItems)
			{
				Item dbItem = dbMap.get(xmlItem.getId());
				if(dbItem == null)
				{
					// INSERT
					Item newItem = new Item(
							xmlItem.getId(),
							xmlItem.getName(),
							xmlItem.getPrice(),
							xmlItem.getOverview(),
							LocalDateTime.now(),
							LocalDateTime.now(),
							xmlItem.getThumbsImageName(),
							xmlItem.getCategoryList(),
							xmlItem.getImagesName());
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
					dbItem.setUpdatedAt(LocalDateTime.now());
					itemMapper.update(dbItem);

					// カテゴリ更新
					categoryMapper.deleteByItemId(dbItem.getId());
					insertCategories(xmlItem);

					// 画像更新
					imageMapper.deleteByItemId(dbItem.getId());
					insertImages(xmlItem);
				}
			}

			// DELETE
			for (Item dbItem : dbItems)
			{
				if(!xmlMap.containsKey(dbItem.getId()))
				{
					categoryMapper.deleteByItemId(dbItem.getId());
					imageMapper.deleteByItemId(dbItem.getId());
					itemMapper.delete(dbItem.getId());
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
		itemMapper.insert(item);
		for (String c : item.getCategoryList())
			categoryMapper.insert(item.getId(), c);
		for (String img : item.getImagesName())
			imageMapper.insert(item.getId(), img);
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
		categoryMapper.deleteByItemId(id);
		imageMapper.deleteByItemId(id);
		itemMapper.delete(id);
	}
}
