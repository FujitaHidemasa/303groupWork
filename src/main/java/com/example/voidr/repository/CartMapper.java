package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Cart;
import com.example.voidr.view.CartView;

@Mapper
public interface CartMapper {

	// ★ヘッダのバッジなどに使う件数
	int countItemsByUserId(@Param("userId") long userId);

	// ★カート合計金額
	int sumTotalByUserId(@Param("userId") long userId);

	// ★表示用JOIN結果
	List<CartView> findViewsByUserId(@Param("userId") long userId);

	// カート削除
	void deleteById(@Param("id") long id);

	// ✅ confirmPurchase() で使う：cartListIdで削除
	void deleteByCartListId(@Param("cartListId") long cartListId);

	// カートに商品を追加・更新
	void upsert(Cart cart);

	// 数量を直接更新
	void updateQuantityByCart(Cart cart);

	// ✅ confirmPurchase() で使う：cartListIdで商品一覧取得
	List<Cart> findByCartListId(@Param("cartListId") long cartListId);

}

