package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.OrderList;

public interface OrderListService {

	/**
	 * 指定したユーザーIDに紐づく注文リストを取得
	 */
	List<OrderList> getOrderListsByUserId(long userId);

	/**
	 * 注文リストを新規作成
	 */
	void createOrderList(OrderList orderList);

	/**
	 * ユーザー名から注文リストを取得（複数件対応）
	 */
	List<OrderList> findByUserName(String username);

	/**
	 * ★管理者用：全ユーザー分の注文リストを取得
	 */
	List<OrderList> getAllOrderListsWithUser();
}
