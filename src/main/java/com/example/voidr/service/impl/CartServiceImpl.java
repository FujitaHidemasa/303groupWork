package com.example.voidr.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.AccessDeniedException; // ★追加：所有チェック失敗で使用
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.voidr.entity.Cart;
import com.example.voidr.entity.CartList;
import com.example.voidr.entity.Item;
import com.example.voidr.repository.CartListMapper;
import com.example.voidr.repository.CartMapper;
import com.example.voidr.service.CartService;
import com.example.voidr.service.ItemService;
import com.example.voidr.view.CartView;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService 
{
	private final CartListMapper cartListMapper;
	private final CartMapper cartMapper;
	private final ItemService itemService;

	@Override
	public List<Cart> getOrCreateCartList(long userId, boolean isLogin) 
	{
		CartList cartList = cartListMapper.findByUserId(userId);
		if (cartList == null) {
			cartList = new CartList();
			cartList.setUserId(userId);
			cartList.setLoginUser(isLogin);
			cartListMapper.insert(cartList);
		}
		return cartMapper.findByCartListId(cartList.getId());
	}

	@Override
	public void saveOrUpdateCart(Cart cart) 
	{
		if (cart.getId() == 0) {
			cartMapper.insert(cart);
		} else {
			cartMapper.update(cart);
		}
		cartListMapper.updateUpdatedAt(cart.getCartListId());
	}

	@Override
	public void deleteGuestCarts() 
	{
		cartMapper.deleteAllGuestCarts();
		cartListMapper.deleteAllGuestCartLists();
	}

	@Override
	public void changeQuantity(Cart cart, Integer count) 
	{
		cartMapper.updateQuantityByCart(cart, count);
		cartListMapper.updateUpdatedAt(cart.getCartListId());
	}

	// ===== ここから UI向け削除機能（非セキュア版：互換維持） =====

	// 10/30 谷口 ★追加：1行だけ削除（ボタン：この商品のみ削除）
	@Override
	@Transactional
	public void deleteItem(long cartId, long cartListId) 
	{
		int n = cartMapper.deleteById(cartId);
		if (n > 0) {
			cartListMapper.updateUpdatedAt(cartListId);
		}
	}

	// ★追加：自分のカートを空にする（ボタン：カートを空にする）
	@Override
	@Transactional
	public void clearMyCart(long cartListId) 
	{
		cartMapper.deleteByCartListId(cartListId);
		cartListMapper.updateUpdatedAt(cartListId);
	}

	@Override
	public List<CartView> getCartViewByUserId(long userId) 
	{
		List<Cart> carts = getOrCreateCartList(userId, true);
		List<CartView> views = new ArrayList<>();
		for (Cart cart : carts) {
			Item item = itemService.getItemById(cart.getItemId());
			views.add(new CartView(cart, item));
		}
		return views;
	}

	// ★追加：cart_list を確実に用意してIDを返す
	@Override
	public long ensureCartListId(long userId, boolean isLogin) 
	{
		var cartList = cartListMapper.findByUserId(userId);
		if (cartList == null) {
			cartList = new CartList();
			cartList.setUserId(userId);
			cartList.setLoginUser(isLogin);
			cartListMapper.insert(cartList);
		}
		return cartList.getId();
	}

	// ===== ここから セキュア版（本人所有チェック込み） =====

	// ★追加：共通ヘルパー（本人所有チェック）
	private void verifyOwnership(long userId, long cartListId) 
	{
		int n = cartListMapper.countByIdAndUserId(cartListId, userId);
		if (n == 0) {
			throw new AccessDeniedException("You do not own this cart list."); // ★追加
		}
	}

	// ★追加：本人チェック込み 1件削除（Controller が呼ぶ）
	@Override
	@Transactional
	public void deleteItemSecured(long userId, long cartId, long cartListId) 
	{
		verifyOwnership(userId, cartListId); // ★追加
		int n = cartMapper.deleteById(cartId);
		if (n > 0) {
			cartListMapper.updateUpdatedAt(cartListId);
		}
	}

	// ★追加：本人チェック込み カート空にする（Controller が呼ぶ）
	@Override
	@Transactional
	public void clearMyCartSecured(long userId, long cartListId) 
	{
		verifyOwnership(userId, cartListId); // ★追加
		cartMapper.deleteByCartListId(cartListId);
		cartListMapper.updateUpdatedAt(cartListId);
	}
}
