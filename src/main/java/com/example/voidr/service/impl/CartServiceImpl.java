package com.example.voidr.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.voidr.entity.Cart;
import com.example.voidr.entity.CartList;
import com.example.voidr.repository.CartListMapper;
import com.example.voidr.repository.CartMapper;
import com.example.voidr.service.CartService;
import com.example.voidr.view.CartView;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

	private final CartMapper cartMapper;
	private final CartListMapper cartListMapper;

	/**
	 * ✅ ユーザーに紐づく CartList を取得 or 作成
	 */
	private long ensureCartListId(long userId) {
		CartList found = cartListMapper.findByUserId(userId);
		if (found != null) {
			return found.getId();
		}

		CartList created = new CartList();
		created.setUserId(userId);
		created.setLoginUser(true);
		cartListMapper.insert(created);
		return created.getId();
	}

	/**
	 * ✅ カートに商品追加（同一商品は数量加算）
	 */
	@Override
	public void addItem(long userId, long itemId, int quantity) {
		int q = Math.max(1, quantity);
		long cartListId = ensureCartListId(userId);

		Cart c = new Cart();
		c.setCartListId(cartListId);
		c.setItemId(itemId);
		c.setQuantity(q);

		// 同一商品なら数量加算、なければ新規追加
		cartMapper.upsert(c);

		// カートリストの更新日時更新
		cartListMapper.touchUpdatedAt(cartListId);
	}

	/**
	 * ✅ 数量変更
	 */
	@Override
	public void changeQuantity(long userId, long cartId, int quantity) {
		int q = Math.max(1, quantity);
		Cart c = new Cart();
		c.setId(cartId);
		c.setQuantity(q);
		cartMapper.updateQuantityByCart(c);
	}

	/**
	 * ✅ カート行削除
	 */
	@Override
	public void remove(long userId, long cartId) {
		cartMapper.deleteById(cartId);
	}

	/**
	 * ✅ カート一覧（JOIN済みビュー）
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CartView> list(long userId) {
		List<CartView> list = cartMapper.findViewsByUserId(userId);
		return (list != null) ? list : java.util.Collections.emptyList();
	}

	/**
	 * ✅ 合計金額
	 */
	@Override
	@Transactional(readOnly = true)
	public int sumTotal(long userId) {
		Integer total = cartMapper.sumTotalByUserId(userId);
		return (total != null) ? total : 0;
	}

	/**
	 * ✅ バッジ表示用件数
	 */
	@Override
	@Transactional(readOnly = true)
	public int countInBadge(long userId) {
		Integer count = cartMapper.countItemsByUserId(userId);
		return (count != null) ? count : 0;
	}

	/**
	 * ✅ ユーザー名でカート取得
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Cart> findByUsername(String username) {
		List<Cart> list = cartMapper.findByUsername(username);
		return (list != null) ? list : java.util.Collections.emptyList();
	}

	/**
	 * ✅ カートクリア
	 */
	@Override
	public void clearCart(String username) {
		cartMapper.deleteAllByUsername(username);
	}
}
