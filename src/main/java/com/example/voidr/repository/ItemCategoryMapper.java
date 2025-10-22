package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ItemCategoryMapper {
	List<String> findByItemId(@Param("itemId") Long itemId);

	void insert(@Param("itemId") Long itemId, @Param("category") String category);

	void deleteByItemId(@Param("itemId") Long itemId);
}
