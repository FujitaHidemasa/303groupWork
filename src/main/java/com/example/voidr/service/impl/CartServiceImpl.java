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

	/** ★変更：ユーザーのcart_listを取得 or 作成（boolean setter名に注意） */
	private long ensureCartListId(long userId) {
		CartList found = cartListMapper.findByUserId(userId);
		if (found != null) {
			return found.getId();
		}
		CartList created = new CartList();
		created.setUserId(userId);
		// ▼ booleanプロパティが isLoginUser の場合は setLoginUser(true) が正しい
		created.setLoginUser(true); // ★変更：setIsLoginUser → setLoginUser
		cartListMapper.insert(created);
		return created.getId();
	}

	@Override
	public void addItem(long userId, long itemId, int quantity) {
		int q = Math.max(1, quantity);
		long cartListId = ensureCartListId(userId);

		Cart c = new Cart();
		// ▼ エンティティのプロパティが cartListId（キャメル）であれば以下
		c.setCartListId(cartListId); // ★変更：setCartlistId → setCartListId
		c.setItemId(itemId);
		c.setQuantity(q);

		// ★追加：UPSERT（同一商品なら加算）
		cartMapper.upsert(c); // ★CartMapper に定義（下で宣言）
		cartListMapper.touchUpdatedAt(cartListId);
	}

	@Override
	public void changeQuantity(long userId, long cartId, int quantity) {
		int q = Math.max(1, quantity);
		Cart c = new Cart();
		c.setId(cartId);
		c.setQuantity(q);
		// 既存のXMLが (cart, quantity) の2引数を想定しているなら下記のようにParam2つの版を呼ぶ
		cartMapper.updateQuantityByCart(c); // ★CartMapper に (Cart) 版を用意（下で宣言）
	}

	@Override
	public void remove(long userId, long cartId) {
		cartMapper.deleteById(cartId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CartView> list(long userId) {
		List<CartView> list = cartMapper.findViewsByUserId(userId);
		return (list != null) ? list : java.util.Collections.emptyList();
	}

	@Override
	@Transactional(readOnly = true)
	public int sumTotal(long userId) {
		return cartMapper.sumTotalByUserId(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public int countInBadge(long userId) {
		return cartMapper.countItemsByUserId(userId);
	}
}
