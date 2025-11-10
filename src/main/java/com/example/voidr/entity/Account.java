package com.example.voidr.entity;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
	
	@NotBlank(message = "ユーザー名は必須です")
	private String username;
	
	@Size(min = 4, message = "パスワードは4文字以上で入力してください。")
	private String password;

	private Role authority;

	@NotBlank(message = "氏名は必須です。")
	private String displayName;

	/** eメール */
	@Email(message = "メールアドレスの形式が正しくありません。")
	private String email;

	/** 住所 */
	private String address;

	/** 電話番号 */
	 @Pattern(regexp = "^[0-9\\-]*$", message = "電話番号は数字とハイフンのみで入力してください。")
	private String phoneNumber;

	/** 作成日 */
	private LocalDateTime createdAt;

	/** 更新日時 */
	private LocalDateTime updatedAt;

}