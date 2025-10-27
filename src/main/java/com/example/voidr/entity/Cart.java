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
	
	/** カートID */
	private long id;
	
	/** カートリストid */
	private long cartListId;
	
	/** カートの商品のid */
	private long itemId;
	
	/** カートの商品の数 */
	private int quantity;
	
	/** カートの商品が「後で買う」状態かどうか */
	private boolean isHold;
	
	/** 更新日 */
	private LocalDateTime updatedAt;
	
}
