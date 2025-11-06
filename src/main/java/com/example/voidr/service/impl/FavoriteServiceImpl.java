package com.example.voidr.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.voidr.entity.Favorite;
import com.example.voidr.repository.FavoriteMapper;
import com.example.voidr.service.FavoriteService;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper; // ✅ Mapperを注入

    /** ユーザーのお気に入り一覧を取得 */
    @Override
    public List<Favorite> getFavoritesByUser(Long userId) {
        return favoriteMapper.findByUserId(userId);
    }

    /** お気に入り追加（重複チェック付き） */
    @Override
    public void addFavorite(Long userId, Long itemId) {
        Favorite existing = favoriteMapper.findByUserIdAndItemId(userId, itemId);
        if (existing == null) {
            Favorite fav = new Favorite();
            fav.setUserId(userId);
            fav.setItemId(itemId);
            favoriteMapper.insertFavorite(fav);
        }
    }

    /** お気に入り削除 */
    @Override
    public void removeFavorite(Long userId, Long itemId) {
        favoriteMapper.deleteFavorite(userId, itemId);
    }

    /** お気に入りON/OFF切り替え */
    @Override
    public boolean toggleFavorite(Long userId, Long itemId) {
        Favorite existing = favoriteMapper.findByUserIdAndItemId(userId, itemId);
        if (existing != null) {
            favoriteMapper.deleteFavorite(userId, itemId);
            return false; // 削除した
        } else {
            Favorite fav = new Favorite();
            fav.setUserId(userId);
            fav.setItemId(itemId);
            favoriteMapper.insertFavorite(fav);
            return true; // 追加した
        }
    }
    
	// ★追加 11/06
	@Override
	public boolean isFavorite(Long userId, Long itemId) {
		return favoriteMapper.findByUserIdAndItemId(userId, itemId) != null;
	}
}
