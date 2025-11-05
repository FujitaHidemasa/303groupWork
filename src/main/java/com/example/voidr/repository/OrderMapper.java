package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.voidr.entity.Order;

@Mapper
public interface OrderMapper {

    List<Order> findByOrderListId(long orderListId);


    void insertOrder(Order order);
}
