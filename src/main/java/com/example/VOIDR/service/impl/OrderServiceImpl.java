package com.example.VOIDR.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.VOIDR.entity.Order;
import com.example.VOIDR.repository.OrderMapper;
import com.example.VOIDR.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

	private final OrderMapper orderMapper;
	
	@Override
	public List<Order> getOrdersByOrderListId(long orderListId) {
		// TODO 自動生成されたメソッド・スタブ
		return orderMapper.findByOrderListId(orderListId);
	}

	@Override
	public void createOrder(Order order) {
		// TODO 自動生成されたメソッド・スタブ
		orderMapper.insert(order);
	}

	

}
