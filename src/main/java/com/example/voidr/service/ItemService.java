package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.Item;

public interface ItemService
{
<<<<<<< HEAD
	/** 商品を全て取得する */
	List<Item> getAllItems();
	
	/** 特定の範囲のidの商品を全て取得する */
	List<Item> getRangeItems(Integer min, Integer max);
	
	/** idに一致した商品を取得する */
	Item getItemById(Long id);
	
	void createItem(Item item);
	
	void updateItem(Item item);
	
	void deleteItem(Long id);
=======
    /**
     * XMLとDBを同期する
     */
    void syncItems();

    /**
     * 全ての商品を取得し、カテゴリもセットして返す
     * @return List<Item>
     */
    List<Item> getAllItems();
>>>>>>> branch 'master' of https://github.com/FujitaHidemasa/303groupWork.git
}
