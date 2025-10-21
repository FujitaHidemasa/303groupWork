package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Cart;

@Mapper
public interface CartMapper {

    List<Cart> findByCartListId(@Param("cartListId") long cartListId);

    void insertCart(Cart cart);

    void updateItemCount(@Param("id") long id,
                         @Param("itemCount") int itemCount,
                         @Param("updatedAt") java.time.LocalDateTime updatedAt);

    void deleteItem(@Param("id") long id);
}