package com.example.VOIDR.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderList {
	
	//
	private long id;
	
	private long userId;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
}
