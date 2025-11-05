package com.example.voidr.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Account;


@Mapper
public interface AccountMapper {

    /** ユーザー名で検索 */
	Account selectByUsername(@Param("username") String username);

    /** 新規ユーザー登録 */
    void insertAccount(Account user);
}