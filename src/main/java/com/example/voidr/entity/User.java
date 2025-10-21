package com.example.voidr.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	/** ユーザーID */
	private long id;

	/** ユーザーネーム */
	private String name;

	/** パスワード(ハッシュ化) */
	private String password;

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

	/** 有効アカウントかどうか */
	private boolean isActive;

}
