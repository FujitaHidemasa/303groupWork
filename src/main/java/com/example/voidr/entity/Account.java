package com.example.voidr.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザー情報:エンティティ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account
{
	private Long id;
	
	private String username;
	
	private String password;

	private Role authority;

	private String displayName;

	/** eメール */
	private String email;

	/** 住所 */
	private String address;

	/** 電話番号 */
	private String phoneNumber;

	/** 作成日 */
	private LocalDateTime createdAt;

	/** 更新日時 */
	private LocalDateTime updatedAt;

}