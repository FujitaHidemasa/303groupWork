package com.example.voidr.service;

import com.example.voidr.entity.User;

public interface UserService {

	User findById(long id);

	User findByEmail(String email);

	void insertUser(User user);

	void updateUser(User user);

}
