package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Item;

@Mapper
public interface ItemMapper
{
	/** 商品を全て取得する */
	List<Item> selectAll();
	
	/** ★管理専用：削除済みも含めて全件取得 */
	List<Item> selectAllIncludingDeleted();
	
    /** 特定の範囲のidの商品を全て取得する */
    List<Item> selectByRangeId(@Param("min") Integer min, @Param("max") Integer max);
    
    /** 特定のカテゴリーに属する商品を全て取得する */
    List<Item> selectByKeyword(@Param("keyword") String keyword);

    /** 特定のカテゴリーに属する商品を全て取得する */
    List<Item> selectByCategory(@Param("category") String category);
    
    /** 最新4件の商品を取得する */
    List<Item> findLatestItems();
	
	/** idに一致した商品を取得する */
	Item selectById(@Param("id")Long id);
	
	/** 商品を新規作成する */
	void insert(Item item);
	
	/** 商品を更新する */
	void update(Item item);
	
	/** 商品を削除する */
	// void delete(@Param("id")Long id);
	
	// ソフトデリート（is_deleted = TRUE に更新）
	void softDelete(Long id);
	
	/** ★管理者専用：販売終了 → 販売再開 */
	void restoreItem(Long id);
	
	/**ランダム4件取得*/
	List<Item> selectRandom4();

	/** 商品件数を返す */
	int countAll();
}
