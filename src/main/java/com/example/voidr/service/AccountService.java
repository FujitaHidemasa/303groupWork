package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.Account;
import com.example.voidr.entity.Role;

public interface AccountService
{
	Account findByUsername(String username);

	// 既存：互換のため残す
	void registerAccount(String username, String rawPassword, Role authority); // 旧

	// 10月２７日 谷口
	// ★追加：schema.sql に合わせた登録（必須: displayName/email、任意: address/phoneNumber）
	void registerAccount(String username, String rawPassword, Role authority,
			String displayName, String email, // ★追加（NOT NULL）
			String address, String phoneNumber); // ★追加（NULL可）

	
	 // 11/10（藤田）追加：会員情報の更新メソッド
    void updateAccount(String username, Account updatedAccount);
    
    //11/10(藤田）会員情報の削除メソッド
    void deleteAccountByUsername(String username);
    
    // 全ユーザー取得（会員一覧ページの初期表示用）
    List<Account> findAll();
    
    // 会員検索：ユーザーID・氏名・メールアドレスの部分一致検索に使用
    List<Account> searchMembers(String keyword);
}


// 変更前
//	/**
//	 * ユーザー名でユーザーを検索
//	 */
//	Account findByUsername(String username);
//
//	/**
//	 * 新規ユーザー登録
//	 */
//	void registerAccount(String username, String rawPassword, Role authority);