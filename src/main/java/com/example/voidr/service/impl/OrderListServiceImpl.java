package com.example.voidr.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.voidr.entity.OrderList;
import com.example.voidr.repository.OrderListMapper;
import com.example.voidr.service.OrderListService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderListServiceImpl implements OrderListService {

	private final OrderListMapper orderListMapper;

	@Override
	public List<OrderList> getOrderListsByUserId(long userId) {
		return orderListMapper.findByUserId(userId);
	}

	@Override
	public void createOrderList(OrderList orderList) {
		if (orderList.getUserId() <= 0) {
			throw new IllegalArgumentException("ユーザーIDが無効です");
		}

		// 注文リストを登録
		orderListMapper.insertOrderList(orderList);
	}

	@Override
	public OrderList findByUserName(String username) {
		// username で検索する必要があれば mapper 側で対応
		throw new UnsupportedOperationException("username での検索は未対応");
	}
}
