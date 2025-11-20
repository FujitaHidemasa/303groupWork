package com.example.voidr.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.voidr.entity.Account;
import com.example.voidr.entity.Role;
import com.example.voidr.repository.AccountMapper;
import com.example.voidr.service.AccountService;

import lombok.RequiredArgsConstructor;


// 10/27 谷口
// ユーザー登録・検索を担当するサービスクラス
// Spring Security + MyBatis連携
@Service
@RequiredArgsConstructor
public class AccountDetailsService implements AccountService
{
    // ★追加：DBアクセスとパスワードハッシュ用
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    // ==========================
    // ユーザー名で検索
    // ==========================
    @Override
    public Account findByUsername(String username)
    {
        // ★DBから1件取得（null許容）
        return accountMapper.findByUsername(username);
    }

    // ==========================
    // 旧シグネチャ互換（呼ばれた場合のフォールバック）
    // ==========================
    @Override
    public void registerAccount(String username, String rawPassword, Role authority)
    {
        // ★最小限NOT NULLを満たすダミーで委譲（不要ならUnsupportedOperationExceptionでも可）
        registerAccount(username, rawPassword, authority,
                        username,                   // displayName
                        username + "@example.com",  // email（ダミー）
                        null, null);                // address/phoneNumber
    }

    // ==========================
    // ★新シグネチャ（実処理）
    // displayName/email 必須、address/phone は任意
    // ==========================
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

    // ==========================
    // ★追加：会員情報更新機能
    // ==========================
    @Transactional
    @Override
    public void updateAccount(String username, Account updatedAccount)
    {
        // ★既存データ取得
        Account existing = accountMapper.findByUsername(username);
        if (existing == null)
        {
            throw new IllegalArgumentException("ユーザーが存在しません: " + username);
        }

        // ★更新可能項目を反映（パスワード・権限は除外）
        existing.setDisplayName(updatedAccount.getDisplayName());
        existing.setEmail(updatedAccount.getEmail());
        existing.setAddress(updatedAccount.getAddress());
        existing.setPhoneNumber(updatedAccount.getPhoneNumber());

        // ★DB更新
        accountMapper.updateAccount(existing);
    }
    
    
    
 // ==========================
    // ★追加：会員情報削除機能
    // ==========================
    
    @Transactional
    @Override
    public void deleteAccountByUsername(String username) {
        accountMapper.deleteByUsername(username);
    }
    
    // =====================================
    //  会員一覧取得（管理画面用）
    // =====================================
    @Override
    public List<Account> findAll() {
        return accountMapper.findAll();
    }
    
    // ==========================
    // 会員検索（ユーザーID／氏名／メールで部分一致検索）
    // 管理画面の検索フォームから利用
    // ==========================
    @Override
    public List<Account> searchMembers(String keyword) {
        return accountMapper.searchMembers(keyword);
    }
    
    //===========================
    //★追加：権限変更機能
    // ==========================
    @Transactional
    @Override
    public void updateAuthority(String username, String authority) {
    	
    	if(!authority.equals("USER") && !authority.equals("ADMIN")) {
    		throw new IllegalArgumentException("不正な権限: " + authority);
    	}
    	
    	// DB反映（Mapper側で UPDATE）
        accountMapper.updateAuthority(username, authority);
    }

}
