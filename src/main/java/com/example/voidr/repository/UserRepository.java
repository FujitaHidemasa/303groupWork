package com.example.voidr.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.voidr.entity.LoginUser;

@Mapper
public interface UserRepository {

    @Select("SELECT id, username, password, authority FROM authentications WHERE username = #{username}")
    LoginUser findByUsername(String username);
}