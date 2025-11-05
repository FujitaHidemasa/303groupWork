package com.example.voidr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ItemImageMapper
{
	List<String> findByItemId(@Param("itemId") Long itemId);

	void insert(@Param("itemId") Long itemId, @Param("imageName") String imageName);

	void deleteByItemId(@Param("itemId") Long itemId);
}
