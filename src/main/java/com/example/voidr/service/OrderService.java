package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.Order;

public interface OrderService {

    /**
     * 購入履歴（商品単位）を取得
     * @param orderListId 購入リストID
     * @return 該当リストの注文一覧
     */
    List<Order> getOrderHistory(long orderListId);

    /**
     * 新しい注文を作成
     * @param order 登録する注文
     */
    void createOrder(Order order);

    /**
     * 注文を確定（購入完了状態に更新）
     * @param orderListId 購入リストID
     */
    void confirmPurchase(long orderListId);
}
