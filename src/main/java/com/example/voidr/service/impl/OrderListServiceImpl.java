package com.example.voidr.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void createOrderList(OrderList orderList) {
        // タイムスタンプを自動セット
        orderList.setCreatedAt(LocalDateTime.now());
        orderList.setUpdatedAt(LocalDateTime.now());
        orderListMapper.insertOrderList(orderList);
    }
}
