package com.example.voidr.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Account;

@Mapper
public interface AccountMapper {

	/** ユーザー名で検索（既存） */
	Account selectByUsername(@Param("username") String username);

	/** 新規ユーザー登録 */
	void insertAccount(Account user);

	/** findByUsername で XML と紐づける */
	Account findByUsername(@Param("username") String username);
}
