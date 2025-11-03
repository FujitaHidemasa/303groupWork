package com.example.voidr.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.CartList;

@Mapper
public interface CartListMapper {

	CartList findByUserId(@Param("userId") long userId);

	void insert(CartList cartList);

	// ★追加：更新時刻だけ触る
	void touchUpdatedAt(@Param("id") long id);
}
