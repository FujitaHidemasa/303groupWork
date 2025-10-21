// 📄 OrderService.java（インターフェース）
package com.example.VOIDR.service;

import java.util.List;

import com.example.VOIDR.entity.Order;

public interface OrderService {
    List<Order> getOrdersByOrderListId(long orderListId);
    void createOrder(Order order);
}
