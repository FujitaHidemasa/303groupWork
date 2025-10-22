package com.example.voidr.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.CartList;

@Mapper
public interface CartListMapper {

    CartList findByUserId(@Param("userId") long userId);

    int insert(CartList cartList);

    int updateUpdatedAt(@Param("id") long id);

    int deleteAllGuestCartLists();
}
