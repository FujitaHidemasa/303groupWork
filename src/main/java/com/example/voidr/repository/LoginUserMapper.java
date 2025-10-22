package com.example.voidr.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.LoginUser;


@Mapper
public interface LoginUserMapper {

    /** ユーザー名で検索 */
    LoginUser findByUsername(@Param("username") String username);

    /** 新規ユーザー登録 */
    void insertUser(LoginUser user);
}