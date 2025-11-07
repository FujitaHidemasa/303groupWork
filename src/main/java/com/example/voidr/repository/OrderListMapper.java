package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.OrderList;
//findByUserId(), insertOrderList()
@Mapper
public interface OrderListMapper {
    // ユーザーIDで購入履歴一覧を取得
    List<OrderList> findByUserId(@Param("userId") long userId);

    // 新しい購入履歴（注文リスト）を作成
    void insertOrderList(OrderList orderList);

	OrderList findByUserName(String username);

}
