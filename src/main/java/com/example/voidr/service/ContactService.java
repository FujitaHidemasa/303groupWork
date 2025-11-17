// ★新規作成：ContactService インターフェース
package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.Contact;

public interface ContactService {

	// ユーザー側：お問い合わせ送信
	void submitContact(Contact contact);

	// 管理画面：全件取得
	List<Contact> findAll();

	// 管理画面：ステータスで絞り込み
	List<Contact> findByStatus(String status);

	// 管理画面：1件取得
	Contact findById(long id);
	
    // 管理画面：1件削除
    void deleteById(long id);

	// 管理画面：ステータス・返信内容の更新
	void updateStatus(long id, String status, String adminReply);
}
