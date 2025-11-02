package com.example.voidr.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.CartList;

@Mapper
public interface CartListMapper 
{
	CartList findByUserId(@Param("userId") long userId);

	void insert(CartList cartList);

	int updateUpdatedAt(@Param("id") long id);

	int deleteAllGuestCartLists();

	// ★追加：所有チェック用（cart_list.id が userId のものか）
	int countByIdAndUserId(@Param("cartListId") long cartListId,
			@Param("userId") long userId); // ★追加
}
