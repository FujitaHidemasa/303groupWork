package com.example.voidr.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.CartList;

@Mapper
public interface CartListMapper {

    CartList findByUserId(@Param("userId") long userId);

    void insert(CartList cartList);

    void updateUpdatedAt(@Param("id") long id);

    void deleteAllGuestCartLists();
}
