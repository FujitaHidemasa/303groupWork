package com.example.voidr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Cart;

@Mapper
public interface CartMapper {

    List<Cart> findByCartListId(@Param("cartListId") long cartListId);

    void insert(Cart cart);

    void update(Cart cart);

    void deleteAllGuestCarts();
    
    void updateQuantityByCart(Cart cart, @Param("count") Integer count);
}
