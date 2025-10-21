package com.example.voidr.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.User;

@Mapper
public interface UserMapper {

    User findById(@Param("id") long id);

    User findByEmail(@Param("email") String email);

    void insertUser(User user);

    void updateUser(User user);
}
