package com.example.VOIDR.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User
{
	/** 主キー */
	private Long id;
	
	/** ユーザー名 */
	private String name;
	
	/** パスワード */
	private String password;
	
	/** Eメール */
	private String email;
	
	/** 住所 */
	private String address;
	
	/** 電話番号 */
	private String phoneNumber;
	
	/** 作成日 */
	private LocalDateTime createdAt;
	
	/** 更新日*/
	private LocalDateTime updatedAt;
	
	/** 有効アカウント */
	private boolean isActive;
}
