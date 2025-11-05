package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.voidr.entity.Favorite;

@Mapper
public interface FavoriteMapper {

	
	//指定ユーザーのお気に入り一覧を取得
	List<Favorite> findByUserId(Long userId);
	
	
	//お気に入りを追加
	void insertFavorite(Favorite favorite);
	
	
	//お気に入りを削除
	void deleteFavorite(Long userId, Long itemId);
	
	
	//すでにお気に入りされてるか確認
	Favorite findByUserIdAndItemId(Long userId, Long itemId);
}
