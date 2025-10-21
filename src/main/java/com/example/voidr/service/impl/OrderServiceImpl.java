package com.example.voidr.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.voidr.entity.Order;
import com.example.voidr.repository.OrderMapper;
import com.example.voidr.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    @Override
    public List<Order> getOrderHistory(long orderListId) {
        return orderMapper.findByOrderListId(orderListId);
    }

    @Override
    @Transactional
    public void createOrder(Order order) {
        orderMapper.insertOrder(order);
    }

    @Override
    @Transactional
    public void confirmPurchase(long orderListId) {
        // 状態(state)を「確定」に更新する処理を後でMyBatisに追加予定
        // 例: UPDATE orders SET state = 'CONFIRMED' WHERE order_list_id = #{orderListId}
        // 仮でログを出すだけにしておく
        System.out.println("注文リストID " + orderListId + " の購入を確定しました。");
    }
}
