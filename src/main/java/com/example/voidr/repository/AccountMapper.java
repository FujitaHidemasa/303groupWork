package com.example.voidr.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Account;

@Mapper
public interface AccountMapper {


    /** ユーザー名で検索 */
	Account findByUsername(@Param("username") String username);


    /** 新規ユーザー登録 */
    void insertAccount(Account user);
    
    /** ユーザー情報の変更**/
    void updateAccount(Account account);
    
    /**ユーザー情報の削除**/
    void deleteByUsername(@Param("username") String username);
}

