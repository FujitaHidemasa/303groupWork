package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.Item;

public interface ItemService
{
	/** 商品を全て取得する */
	List<Item> getAllItems();
	
	/** 特定の範囲のidの商品を全て取得する */
	List<Item> getRangeItems(Integer min, Integer max);
	
	/** idに一致した商品を取得する */
	Item getItemById(Long id);
	
	void createItem(Item item);
	
	void updateItem(Item item);
	
	void deleteItem(Long id);
}
