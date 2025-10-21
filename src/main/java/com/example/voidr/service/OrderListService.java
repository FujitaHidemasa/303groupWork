package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.OrderList;

public interface OrderListService {
    List<OrderList> getOrderListsByUserId(long userId);
    
    void createOrderList(OrderList orderList);
}
