package com.example.voidr.entity;

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
	// totalPrice は削除
	private String paymentMethod; // 支払い方法
	private String address; // 住所
	private LocalDateTime createdAt; // 作成日時
	private LocalDateTime updatedAt; // 更新日時
	public void setTotalPrice(int totalPrice) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}
