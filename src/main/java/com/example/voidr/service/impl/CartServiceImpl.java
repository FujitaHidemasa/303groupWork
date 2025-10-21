package com.example.voidr.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.voidr.entity.Cart;
import com.example.voidr.repository.CartMapper;
import com.example.voidr.service.CartService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService{
	
	private final CartMapper cartMapper;

	@Override
	public List<Cart> findByCartListId(long cartListId) {
		return cartMapper.findByCartListId(cartListId);
	}

	@Override
	public void insertCart(Cart cart) {
		cartMapper.insertCart(cart);
	}

	@Override
	public void updateItemCount(long id, int itemCount, LocalDateTime updatedAt) {
		cartMapper.updateItemCount(id, itemCount, updatedAt);
		
	}

	@Override
	public void deleteItem(long id) {
		cartMapper.deleteItem(id);
		
	}

}
