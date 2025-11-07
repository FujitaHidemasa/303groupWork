package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.Cart;
import com.example.voidr.view.CartView;

public interface CartService {
	// ★追加：アイテム追加（数量指定、同一商品は加算）
	void addItem(long userId, long itemId, int quantity);

	// ★追加：数量直接変更（カート行ID指定）
	void changeQuantity(long userId, long cartId, int quantity);

	// ★追加：行削除
	void remove(long userId, long cartId);

	// ★追加：一覧と合計・件数
	List<CartView> list(long userId);

	int sumTotal(long userId);

	int countInBadge(long userId);

	List<Cart> findByUsername(String username);

	void clearCart(String username);
}
