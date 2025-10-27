package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.Cart;
import com.example.voidr.view.CartView;

public interface CartService {

    List<Cart> getOrCreateCartList(long userId, boolean isLogin);

    void saveOrUpdateCart(Cart cart);

    void deleteGuestCarts();
    
    List<CartView> getCartViewByUserId(long userId);
    
    void changeQuantity(Cart cart,  Integer count);
}
