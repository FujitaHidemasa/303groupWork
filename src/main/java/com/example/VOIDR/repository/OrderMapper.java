package com.example.VOIDR.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.VOIDR.entity.Order;

@Mapper
public interface OrderMapper {

    List<Order> findByOrderListId(long orderListId);


    void insert(Order order);
}
