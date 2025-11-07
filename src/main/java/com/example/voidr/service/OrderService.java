package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.Order;

public interface OrderService {

	/** 購入履歴を取得 */
	List<Order> getOrderHistory(long orderListId);

	/** 注文登録 */
	void createOrder(Order order);

	/** 購入確定 */
	void confirmPurchase(long orderListId);
}
