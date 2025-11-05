package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Cart;
import com.example.voidr.view.CartView;

@Mapper
public interface CartMapper 
{

	// ★追加：ヘッダのバッジなどに使う件数
	int countItemsByUserId(@Param("userId") long userId);

	// ★追加：カート合計金額
	int sumTotalByUserId(@Param("userId") long userId);

	// ★追加：表示用JOIN結果
	List<CartView> findViewsByUserId(@Param("userId") long userId);

	// 既存：削除
	void deleteById(@Param("id") long id);

	void deleteByCartListId(@Param("cartListId") long cartListId);

	// ★追加：UPSERT（XML側で ON CONFLICT を定義）
	void upsert(Cart cart);

	// ★追加：数量を直接セットする更新（XMLは単引数版に合わせる）
	void updateQuantityByCart(Cart cart);
}
