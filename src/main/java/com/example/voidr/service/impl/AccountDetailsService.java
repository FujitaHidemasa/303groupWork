package com.example.voidr.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.voidr.entity.Account;
import com.example.voidr.entity.Role;
import com.example.voidr.repository.AccountMapper;
import com.example.voidr.service.AccountService;

import lombok.RequiredArgsConstructor;


// 10/27 谷口

@Service
@RequiredArgsConstructor
public class AccountDetailsService implements AccountService
{
	   // ★追加：DBアクセスとパスワードハッシュ用
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Account findByUsername(String username)
    {
        // ★DBから1件取得（null許容）
        return accountMapper.selectByUsername(username);
    }

    // 旧シグネチャ互換（呼ばれた場合のフォールバック）
    @Override
    public void registerAccount(String username, String rawPassword, Role authority)
    {
        // ★最小限NOT NULLを満たすダミーで委譲（不要ならUnsupportedOperationExceptionでも可）
        registerAccount(username, rawPassword, authority,
                        username,                   // displayName
                        username + "@example.com",  // email（ダミー）
                        null, null);                // address/phoneNumber
    }

    // ★新シグネチャ（実処理）：displayName/email 必須、address/phone は任意
    @Transactional
    @Override
    public void registerAccount(String username, String rawPassword, Role authority,
                                String displayName, String email,
                                String address, String phoneNumber)
    {
        Account a = new Account();
        a.setUsername(username);
        a.setPassword(passwordEncoder.encode(rawPassword));

        // ★DBのENUMは 'ADMIN' / 'USER'。Roleは "ROLE_ADMIN/ROLE_USER" なので変換
        // 修正後（★enumのままセット）
        Role dbAuthority = (authority == Role.ADMIN) ? Role.ADMIN : Role.USER;  // ★変更
        a.setAuthority(dbAuthority);                                            // ★変更

        a.setDisplayName(displayName);  // NOT NULL
        a.setEmail(email);              // NOT NULL

        // 任意（NULL可）
        a.setAddress(address);
        a.setPhoneNumber(phoneNumber);

        // ★INSERT
        accountMapper.insertAccount(a);
    }

}


// 変更前 10/27
//	@Override
//	public Account findByUsername(String username)
//	{
//		return null;
//	}
//
//	@Override
//	public void registerAccount(String username, String rawPassword, Role authority)
//	{
//	}