package com.example.voidr.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

	// 注文ID（主キー）
	private long id;

	// 対応する購入履歴リストID
	private long orderListId;

	// 商品ID
	private long itemId;

	// 後で買うフラグ
	private boolean isHold;

	// 作成日（注文日時）
	private LocalDateTime createdAt;

	// ===============================
	// 以下は購入履歴画面表示用の追加フィールド
	// ===============================

	// 商品名
	private String itemName;

	// 商品画像（サムネイルファイル名など）
	private String imageName;

	// 購入価格
	private int price;

	// 購入数量
	private int quantity;
}
