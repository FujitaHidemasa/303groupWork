package com.example.voidr.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

/**	10月２７日 谷口
 * 
 * 新規ユーザー登録フォーム。
 * ユーザー登録時の入力チェック（名前・パスワードの長さ・必須項目）
 * username, password のバリデーションを実施。
 */

@Data
public class SignupForm 
{

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;        // login_user.username

	// 11/13変更（谷口） 8〜20に統一＋英数字のみ
	@NotBlank
	@Size(min = 8, max = 20, message = "パスワードは8〜20文字で入力してください。")
	@Pattern(regexp = "^[A-Za-z0-9]+$", message = "パスワードは英数字のみ使用できます。")
	private String password; // 平文 → 保存時にBCrypt

    @NotBlank
    @Size(max = 50)
    private String displayName;     // login_user.display_name (NOT NULL)

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;           // login_user.email (NOT NULL)
    
    // =============================
    //  住所・電話番号
    //	任意入力（NULL可）だが、入力された場合は制限する
    // =============================

    @Size(max = 255, message = "住所は255文字以内で入力してください") // ★追加：schemaに合わせた上限
    private String address;                                        // ★追加

    @Size(max = 20, message = "電話番号は20文字以内で入力してください") // ★追加：桁上限
    @Pattern
    (                                                     // ★追加：許容文字（数字/ハイフン/空白/() / +）
        regexp = "^[0-9\\-\\+()\\s]*$",
        message = "電話番号は数字・+・-・( )・空白のみ使用できます"
    )
    private String phoneNumber;                                   // ★追加
}



