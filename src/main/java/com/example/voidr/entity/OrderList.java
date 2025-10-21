package com.example.voidr.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderList {
	
	//id 主キー
	private long id;
	
	//ユーザーid
	private long userId;
	
	//作成日
	private LocalDateTime createdAt;
	
	//更新日
	private LocalDateTime updatedAt;
}
