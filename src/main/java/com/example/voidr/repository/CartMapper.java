package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Cart;

@Mapper
public interface CartMapper {

    List<Cart> findByCartListId(@Param("cartListId") long cartListId);

    int insert(Cart cart);

    int update(Cart cart);

    int deleteAllGuestCarts();
}
