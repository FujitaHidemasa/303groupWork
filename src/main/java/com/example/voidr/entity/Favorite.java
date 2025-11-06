package com.example.voidr.entity;

import java.time.LocalDateTime;

import lombok.Data;

//お気に入り情報エンティティ
@Data
public class Favorite {
	
	private Long id;
	
	
	private Long userId;
	
	
	private Long itemId;
	
	
	private LocalDateTime createdAt;
	
	

}
