package com.example.voidr.service;

import com.example.voidr.entity.Account;
import com.example.voidr.entity.Role;

public interface AccountService
{

	/**
	 * ユーザー名でユーザーを検索
	 */
	Account findByUsername(String username);

	/**
	 * 新規ユーザー登録
	 */
	void registerAccount(String username, String rawPassword, Role authority);
}