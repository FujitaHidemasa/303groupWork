package com.example.voidr.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注文ごとの商品明細（OrderItem）エンティティ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

	// 主キー
	private long id;

	// 注文（order）ID
	private long orderId;

	// 商品ID
	private long itemId;

	// 購入時の単価
	private int price;

	// 購入数量
	private int quantity;

	// 作成日時
	private LocalDateTime createdAt;
}
