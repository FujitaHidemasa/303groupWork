package com.example.voidr.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.voidr.entity.OrderItem;
import com.example.voidr.repository.OrderItemMapper;
import com.example.voidr.service.OrderItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

	private final OrderItemMapper orderItemMapper;

	@Override
	public List<OrderItem> getOrderItemsByOrderId(long orderId) {
		return orderItemMapper.findByOrderId(orderId);
	}

	@Override
	@Transactional
	public void addOrderItem(OrderItem orderItem) {
		orderItemMapper.insertOrderItem(orderItem);
	}
}
