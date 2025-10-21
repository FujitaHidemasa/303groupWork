package com.example.voidr.repository;

import java.util.List;

import com.example.voidr.entity.Item;

public interface ItemMapper
{
	/** 商品を全て取得する */
	List<Item> selectAll();
	
	/** 特定の範囲のidの商品を全て取得する */
	List<Item> selectByRangeId(Integer min, Integer max);
	
	/** 特定のカテゴリーに属する商品を全て取得する */
	List<Item> selectByCategory(String category);
	
	/** idに一致した商品を取得する */
	Item selectById(Long id);
	
	void insert(Item item);
	
	void update(Item item);
	
	void delete(Long id);
}
