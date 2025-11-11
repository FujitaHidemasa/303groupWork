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
		orderListMapper.insertOrderList(orderList);
	}

	@Override
	public List<OrderList> findByUserName(String username) {
		// 複数件対応
		return orderListMapper.findByUserName(username);
	}

}
