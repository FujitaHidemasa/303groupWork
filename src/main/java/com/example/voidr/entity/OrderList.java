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

	public void setUsername(String username) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	public void setTotalPrice(int totalPrice) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	public void setPaymentMethod(String paymentMethod) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	public String getUsername() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public void setAccountId(long userId2) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}
