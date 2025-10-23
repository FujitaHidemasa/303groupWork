package com.example.voidr.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

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
		if(cartList == null)
		{
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
		if(cart.getId() == 0)
		{
			cartMapper.insert(cart);
		}
		else
		{
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
	public List<CartView> getCartViewByUserId(long userId) {
	    List<Cart> carts = getOrCreateCartList(userId, true);
	    List<CartView> views = new ArrayList<>();
	    carts.forEach(e -> System.out.println());
	    for (Cart cart : carts) {
	        Item item = itemService.getItemById(cart.getItemId());
	        views.add(new CartView(cart, item));
	    }
	    return views;
	}

}
