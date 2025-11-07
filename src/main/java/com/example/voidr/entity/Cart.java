package com.example.voidr.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * カート:エンティティ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

	private long id; // カートID
	private long cartListId; // カートリストID
	private long itemId; // 商品ID
	private int quantity; // 数量
	private boolean isHold; // 後で買うフラグ
	private LocalDateTime updatedAt; // 更新日

	/** 商品情報（JOIN で取得用） */
	private Item item; // ← ★追加！

}
