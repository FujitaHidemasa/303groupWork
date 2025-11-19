package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.Item;

public interface ItemService
{
    /** 全商品取得 */
    List<Item> getAllItems();
    
    /** 削除済み商品を含めて全件取得(管理者用)
     * @return is_deleted を含む全商品の一覧 */
    List<Item> getAllItemsIncludingDeleted();

    /** idの範囲で商品取得 */
    List<Item> getItemsByRangeId(Integer min, Integer max);

    /** カテゴリーで商品取得 */
    List<Item> getItemsByCategory(String category);
    
    /** キーワードで商品取得 */
    List<Item> searchItemsByKeyword(String keyword);
    
    /** 最新4件の商品を取得 */
    List<Item> getLatestItems();

    /** idで商品取得 */
    Item getItemById(Long id);

    /** 商品新規作成 */
    void createItem(Item item);

    /** 商品更新 */
    void updateItem(Item item);

    /** 商品をソフトデリート（is_deleted = TRUE）にする */
    void deleteItem(Long id);
    
    /** ★管理者専用：販売終了 → 販売再開 */
    void restoreItem(Long id);
	
    /**
     * XMLとDBを同期する
     */
    void syncItems();
    
    /*ランダムで4件取得*/
    
    public List<Item> getRandom4Items();

    

}
