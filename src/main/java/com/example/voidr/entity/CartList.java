package com.example.voidr.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartList {
	
	/** カートリストid */
	private long id;
	
	/** カートのユーザーid */
	private long userId;
	
	/** ログイン状態か */
	private boolean isLoginUser;
	
	/** 作成日 */
	private LocalDateTime created_at;
	
	/** 更新日 */
	private LocalDateTime updated_at;
	
}
