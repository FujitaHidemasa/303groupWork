package com.example.voidr.service.impl;

import org.springframework.stereotype.Service;

import com.example.voidr.entity.CartList;
import com.example.voidr.repository.CartListMapper;
import com.example.voidr.service.CartListService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CartListServiceImpl implements CartListService{
	
	private final CartListMapper cartListMapper;

	@Override
	public CartList findByUserId(long user_id) {
		return cartListMapper.findByUserId(user_id);
	}

	@Override
	public void insertCartList(CartList cartList) {
		cartListMapper.insertCartList(cartList);
		
	}
	
	

}
