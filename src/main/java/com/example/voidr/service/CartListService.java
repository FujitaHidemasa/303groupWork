package com.example.voidr.service;


import com.example.voidr.entity.CartList;

public interface CartListService {
	
    CartList findByUserId(long user_id);

    void insertCartList(CartList cartList);

}
