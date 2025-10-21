package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Item;

public interface ItemMapper
{
	/** 商品を全て取得する */
	List<Item> selectAll();
	
    /** 特定の範囲のidの商品を全て取得する */
    List<Item> selectByRangeId(@Param("min") Integer min, @Param("max") Integer max);

    /** 特定のカテゴリーに属する商品を全て取得する */
    List<Item> selectByCategory(@Param("category") String category);
	
	/** idに一致した商品を取得する */
	Item selectById(@Param("id")Long id);
	
	/** 商品を新規作成する */
	void insert(Item item);
	
	/** 商品を更新する */
	void update(Item item);
	
	/** 商品を削除する */
	void delete(@Param("id")Long id);
}
