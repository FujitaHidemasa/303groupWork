package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.OrderItem;

public interface OrderItemService {

	List<OrderItem> getOrderItemsByOrderId(long orderId);

	void addOrderItem(OrderItem orderItem);
}
