package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.Cart;
import com.example.voidr.view.CartView;

public interface CartService {

	/** アイテム追加（数量指定、同一商品は加算） */
	void addItem(long userId, long itemId, int quantity);

	/** アイテム追加（ユーザー名指定） */
	void addItem(String username, long itemId, int quantity);

	/** 数量直接変更（カート行ID指定） */
	void changeQuantity(long userId, long cartId, int quantity);

	/** カート行削除 */
	void remove(long userId, long cartId);

	/** カート一覧（JOIN済ビュー） */
	List<CartView> list(long userId);

	/** 合計金額 */
	int sumTotal(long userId);

	/** バッジ表示用件数 */
	int countInBadge(long userId);

	/** ユーザー名でカート取得 */
	List<Cart> findByUsername(String username);

	/** カートクリア */
	void clearCart(String username);
}
