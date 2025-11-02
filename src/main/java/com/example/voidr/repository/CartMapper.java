package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Cart;

@Mapper
public interface CartMapper 
{
	List<Cart> findByCartListId(@Param("cartListId") long cartListId);

	void insert(Cart cart);

	void update(Cart cart);

	// ★修正：count と cart.id を XML 側で使うため @Param を付与
	void updateQuantityByCart(@Param("cart") Cart cart, @Param("count") Integer count); // ★修正

	// ===== 削除系 =====
	int deleteById(@Param("id") long id); // ★追加：1件削除

	int deleteByCartListId(@Param("cartListId") long cartListId); // ★追加：特定カートを空にする

	int deleteAllGuestCarts(); // ★追加：全ゲスト一括削除（清掃用）
}
