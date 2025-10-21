package com.example.voidr.service.impl;

import org.springframework.stereotype.Service;

import com.example.voidr.entity.User;
import com.example.voidr.repository.UserMapper;
import com.example.voidr.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{
	
	private final UserMapper userMapper;

	@Override
	public User findById(long id) {
		return userMapper.findById(id);
	}

	@Override
	public User findByEmail(String email) {
		return userMapper.findByEmail(email);
	}

	@Override
	public void insertUser(User user) {
		userMapper.insertUser(user);		
	}

	@Override
	public void updateUser(User user) {
		userMapper.updateUser(user);		
	}
	
	
}
