package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Order;

@Mapper
public interface OrderMapper {

	/** 指定ユーザーの注文履歴を取得 */
	List<Order> findByOrderListId(long orderListId);

	/** 新しい注文を追加 */
	void insertOrder(Order order);

	/** 購入確定状態に変更 */
	void confirmPurchase(@Param("orderListId") long orderListId);
}
