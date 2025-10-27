package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.Item;

public interface ItemService
{
    /** 全商品取得 */
    List<Item> getAllItems();

    /** idの範囲で商品取得 */
    List<Item> getItemsByRangeId(Integer min, Integer max);

    /** カテゴリーで商品取得 */
    List<Item> getItemsByCategory(String category);
    
    /** キーワードで商品取得 */
    List<Item> searchItemsByKeyword(String keyword);

    /** idで商品取得 */
    Item getItemById(Long id);

    /** 商品新規作成 */
    void createItem(Item item);

    /** 商品更新 */
    void updateItem(Item item);

    /** 商品削除 */
    void deleteItem(Long id);
	
    /**
     * XMLとDBを同期する
     */
    void syncItems();
}
