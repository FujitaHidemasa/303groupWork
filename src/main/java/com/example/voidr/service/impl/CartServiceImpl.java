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
public class CartServiceImpl implements CartService 
{

	private final CartMapper cartMapper;
	private final CartListMapper cartListMapper;

	/** ユーザーの cart_list を取得 or 作成 */
	private long ensureCartListId(long userId) {
		CartList found = cartListMapper.findByUserId(userId);
		if (found != null) {
			return found.getId();
		}
		CartList created = new CartList();
		created.setUserId(userId);
		// boolean プロパティが isLoginUser の場合は setter は setLoginUser(...)
		created.setLoginUser(true);
		cartListMapper.insert(created);
		return created.getId();
	}

	@Override
	public void addItem(long userId, long itemId, int quantity) 
	{
		int q = Math.max(1, quantity);
		long cartListId = ensureCartListId(userId);

		Cart c = new Cart();
		c.setCartListId(cartListId); // ※ キャメルケース
		c.setItemId(itemId);
		c.setQuantity(q);

		cartMapper.upsert(c); // 同一商品は数量加算
		cartListMapper.touchUpdatedAt(cartListId);
	}

	@Override
	public void changeQuantity(long userId, long cartId, int quantity) 
	{
		int q = Math.max(1, quantity);
		Cart c = new Cart();
		c.setId(cartId);
		c.setQuantity(q);
		cartMapper.updateQuantityByCart(c);
	}

	@Override
	public void remove(long userId, long cartId) {
		cartMapper.deleteById(cartId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CartView> list(long userId) 
	{
		List<CartView> list = cartMapper.findViewsByUserId(userId);
		return (list != null) ? list : java.util.Collections.emptyList();
	}

	@Override
	@Transactional(readOnly = true)
	public int sumTotal(long userId) 
	{
		return cartMapper.sumTotalByUserId(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public int countInBadge(long userId) 
	{
		return cartMapper.countItemsByUserId(userId);
	}
}
