package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.Cart;
import com.example.voidr.view.CartView;

public interface CartService 
{
	List<Cart> getOrCreateCartList(long userId, boolean isLogin);

	void saveOrUpdateCart(Cart cart);

	void changeQuantity(Cart cart, Integer count);

	void deleteGuestCarts();

	// UI用（本人チェックなし版。既存互換）
	void deleteItem(long cartId, long cartListId);

	void clearMyCart(long cartListId);

	// ★追加：カートリストIDを確実に取得（無ければ作る）
	long ensureCartListId(long userId, boolean isLogin); // ★追加

	// ★追加：本人所有チェック込み（Controllerからはこちらを呼ぶ）
	void deleteItemSecured(long userId, long cartId, long cartListId); // ★追加

	void clearMyCartSecured(long userId, long cartListId); // ★追加

	List<CartView> getCartViewByUserId(long userId);
}
