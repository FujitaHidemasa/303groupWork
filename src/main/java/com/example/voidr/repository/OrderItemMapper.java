package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.OrderItem;

@Mapper
public interface OrderItemMapper {

	/** 指定した注文IDに紐づく商品明細を取得 */
	List<OrderItem> findByOrderId(@Param("orderId") long orderId);

	/** 注文に商品を追加 */
	void insertOrderItem(OrderItem orderItem);
}
