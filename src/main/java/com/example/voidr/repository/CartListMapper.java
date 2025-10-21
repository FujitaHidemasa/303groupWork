package com.example.voidr.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.CartList;

@Mapper
public interface CartListMapper {

    CartList findByUserId(@Param("user_id") long user_id);

    void insertCartList(CartList cartList);
}
