package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Favorite;

@Mapper
public interface FavoriteMapper {

	// 追加★ 谷口 11/06 @Param付与
	//指定ユーザーのお気に入り一覧を取得
	List<Favorite> findByUserId(@Param("userId") Long userId);
	
	
	//お気に入りを追加
	void insertFavorite(Favorite favorite);
	
	
	//お気に入りを削除
	void deleteFavorite(@Param("userId")Long userId, @Param("itemId")Long itemId);
	
	
	//すでにお気に入りされてるか確認
	Favorite findByUserIdAndItemId(@Param("userId")Long userId, @Param("itemId")Long itemId);
}
