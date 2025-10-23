package com.example.voidr.service;

import com.example.voidr.entity.LoginUser;
import com.example.voidr.entity.Role;

public interface LoginUserService
{

	/**
	 * ユーザー名でユーザーを検索
	 */
	LoginUser findByUsername(String username);

	/**
	 * 新規ユーザー登録
	 */
	void registerUser(String username, String rawPassword, Role authority);
}