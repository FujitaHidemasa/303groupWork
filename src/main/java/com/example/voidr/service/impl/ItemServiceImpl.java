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
import com.example.voidr.repository.ItemMapper;
import com.example.voidr.service.ItemService;
import com.example.voidr.xml.ItemXmlInfo;
import com.example.voidr.xml.ItemXmlRoot;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
	
	private final ItemMapper itemMapper;
	
	private final ItemCategoryMapper categoryMapper;

	@PostConstruct
	@Override
	public void syncItems() {
		try {
			if(!FilePath.ITEM_LIST_XML.exists())
			{
				throw new Exception("ItemList.xmlが存在しません:"+ FilePath.ITEM_LIST_XML.getRelativePath());
			}
			ItemXmlRoot xmlData = XmlHelper.loadXmlClass(FilePath.ITEM_LIST_XML.getRelativePath(), ItemXmlRoot.class);
			List<ItemXmlInfo> xmlItems = xmlData.getItems();
			List<Item> dbItems = getAllItems();

			Map<Long, Item> dbMap = dbItems.stream()
					.collect(Collectors.toMap(Item::getId, i -> i));
			Map<Long, ItemXmlInfo> xmlMap = xmlItems.stream()
					.collect(Collectors.toMap(ItemXmlInfo::getId, i -> i));

			// INSERT or UPDATE
			for (ItemXmlInfo xmlItem : xmlItems) {
				Item dbItem = dbMap.get(xmlItem.getId());
				if (dbItem == null) {
					// INSERT
					Item newItem = new Item(
							xmlItem.getId(),
							xmlItem.getName(),
							xmlItem.getPrice(),
							xmlItem.getOverview(),
							LocalDateTime.now(),
							LocalDateTime.now(),
							xmlItem.getCategoryList());
					itemMapper.insert(newItem);
					insertCategories(xmlItem);
				} else if (!isSame(dbItem, xmlItem)) {
					// UPDATE
					dbItem.setName(xmlItem.getName());
					dbItem.setPrice(xmlItem.getPrice());
					dbItem.setOverview(xmlItem.getOverview());
					dbItem.setUpdatedAt(LocalDateTime.now());
					itemMapper.update(dbItem);

					// カテゴリ更新
					categoryMapper.deleteByItemId(dbItem.getId());
					insertCategories(xmlItem);
				}
			}

			// DELETE
			for (Item dbItem : dbItems) {
				if (!xmlMap.containsKey(dbItem.getId())) {
					categoryMapper.deleteByItemId(dbItem.getId());
					itemMapper.delete(dbItem.getId());
				}
			}

			System.out.println("✅ XML ↔ DB 同期完了");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void insertCategories(ItemXmlInfo xmlItem) {
		List<String> list = xmlItem.getCategoryList();
		if (list != null && !list.isEmpty()) {
			for (String c : list) {
				categoryMapper.insert(xmlItem.getId(), c);
			}
		}
	}

	private boolean isSame(Item dbItem, ItemXmlInfo xmlItem) {
		// 1. 基本情報の比較
		boolean basicMatch = Objects.equals(dbItem.getName(), xmlItem.getName())
				&& dbItem.getPrice() == xmlItem.getPrice()
				&& Objects.equals(dbItem.getOverview(), xmlItem.getOverview());

		if (!basicMatch) {
			return false;
		}

		// 2. カテゴリ比較
		List<String> dbCategories = categoryMapper.findByItemId(dbItem.getId());
		List<String> xmlCategories = xmlItem.getCategoryList() != null
				? xmlItem.getCategoryList()
				: List.of();

		// DBとXMLのカテゴリをソートして順序無視で比較
		List<String> dbSorted = new ArrayList<>(dbCategories);
		List<String> xmlSorted = new ArrayList<>(xmlCategories);

		Collections.sort(dbSorted);
		Collections.sort(xmlSorted);

		return dbSorted.equals(xmlSorted);
	}

	@Override
	public List<Item> getAllItems()
	{
		List<Item> dbItems = itemMapper.selectAll();
		for (Item item : dbItems) {
			List<String> categories = categoryMapper.findByItemId(item.getId());
			item.setCategoryList(categories);
		}
		return dbItems;
	}
}
