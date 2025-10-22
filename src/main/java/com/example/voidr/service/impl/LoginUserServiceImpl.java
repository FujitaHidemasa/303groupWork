package com.example.voidr.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.voidr.entity.LoginUser;
import com.example.voidr.entity.Role;
import com.example.voidr.repository.LoginUserMapper;
import com.example.voidr.service.LoginUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginUserServiceImpl implements LoginUserService
{

	private final LoginUserMapper loginUserMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public LoginUser findByUsername(String username)
	{
		return loginUserMapper.findByUsername(username);
	}

	@Override
	public void registerUser(String username, String rawPassword, Role authority)
	{
		LoginUser user = new LoginUser();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(rawPassword));
		user.setAuthority(authority);
		loginUserMapper.insertUser(user);
	}
}