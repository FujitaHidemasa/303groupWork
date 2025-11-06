package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.Favorite;

public interface FavoriteService {
	
	//指定ユーザーのお気に入り一覧を取得
	List<Favorite> getFavoritesByUser(Long userId);
	
	
	//お気に入りを追加
	void addFavorite(Long userId, Long itemId);
	
	
	//お気に入りを削除
	void removeFavorite(Long userId, Long itemId);
	
	
	boolean toggleFavorite(Long userId, Long itemId);

	// ★追加 11/06
	boolean isFavorite(Long userId, Long itemId);
}
