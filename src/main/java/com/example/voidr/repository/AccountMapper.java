package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Account;

@Mapper
public interface AccountMapper {


    /** ユーザー名で検索 */
	Account findByUsername(@Param("username") String username);
	
	/** ユーザーID・氏名・メールアドレスの部分一致検索（会員管理検索用） */
	List<Account> searchMembers(@Param("keyword") String keyword);

    /** 新規ユーザー登録 */
    void insertAccount(Account user);
    
    /** ユーザー情報の変更**/
    void updateAccount(Account account);
    
    /**ユーザー情報の削除**/
    void deleteByUsername(@Param("username") String username);
    
    /** 全ユーザー取得 */
    List<Account> findAll();
    
	/** 11/13追加（谷口） メールアドレスで検索 */
	Account findByEmail(@Param("email") String email);

	/** 11/13追加（谷口） メールアドレスでパスワード更新 */
	int updatePasswordByEmail
	(	@Param("email") String email, 
		@Param("passwordHash") String passwordHash);
	
	/** 権限（ADMIN / USER）の更新 */
	void updateAuthority(
	        @Param("username") String username,
	        @Param("authority") String authority);

}

