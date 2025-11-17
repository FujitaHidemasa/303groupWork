// ★新規作成：ContactMapper
package com.example.voidr.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Contact;

@Mapper
public interface ContactMapper {

	// 新規登録
	void insert(Contact contact);

	// 全件取得（管理画面用）
	List<Contact> findAll();

	// ステータスで絞り込み（管理画面用）
	List<Contact> findByStatus(@Param("status") String status);

	// 1件取得
	Contact findById(@Param("id") long id);
	
	// 1件削除
	void deleteById(@Param("id") long id);

	// ステータス・返信内容の更新
	void updateStatus(
			@Param("id") long id,
			@Param("status") String status,
			@Param("adminReply") String adminReply,
			@Param("repliedAt") LocalDateTime repliedAt);
}
