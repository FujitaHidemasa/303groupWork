package com.example.voidr.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	
	//id 主キー
	private long id;
	
	//対応する購入履歴リストid
	private long orderListId;
	
	//購入した商品id
	private long itemId;
	
	//購入した商品が「後で買う」状態かどうか
	private boolean isHold;
	
	//作成日
	private LocalDateTime createdAt;
}
