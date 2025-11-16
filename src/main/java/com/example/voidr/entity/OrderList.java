package com.example.voidr.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderList {

	private long id; // 注文リストID
	private long userId; // ユーザーID
	private String username; // ユーザーネーム

	
	/** 購入画面の入力内容 */
	private String status; // 注文ステータス
	private String paymentMethod;
	private String address;
	private LocalDate deliveryDate;
	private String deliveryTime;
	
	private LocalDateTime createdAt; // 作成日時
	private LocalDateTime updatedAt; // 更新日時
}
