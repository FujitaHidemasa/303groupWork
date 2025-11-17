// ★新規作成：Contact エンティティ
package com.example.voidr.entity;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * お問い合わせ情報
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

	private Long id;

	// ログインユーザーの場合のみ設定。ゲスト問い合わせは null
	private Long userId;

	@NotBlank(message = "お名前は必須です。")
	@Size(max = 100, message = "お名前は100文字以内で入力してください。")
	private String name;

	@NotBlank(message = "メールアドレスは必須です。")
	@Email(message = "メールアドレスの形式が正しくありません。")
	@Size(max = 100, message = "メールアドレスは100文字以内で入力してください。")
	private String email;

	@NotBlank(message = "件名は必須です。")
	@Size(max = 200, message = "件名は200文字以内で入力してください。")
	private String subject;

	@NotBlank(message = "お問い合わせ内容は必須です。")
	@Size(max = 2000, message = "お問い合わせ内容は2000文字以内で入力してください。")
	private String message;

	// NEW（未対応）、IN_PROGRESS（対応中）、RESOLVED（対応済）
	private String status;

	// 管理者側からの返信内容
	private String adminReply;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime repliedAt;
}