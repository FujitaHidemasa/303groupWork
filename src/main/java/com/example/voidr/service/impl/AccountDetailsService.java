package com.example.voidr.service.impl;

import org.springframework.stereotype.Service;

import com.example.voidr.entity.Account;
import com.example.voidr.entity.Role;
import com.example.voidr.service.AccountService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountDetailsService implements AccountService
{

	@Override
	public Account findByUsername(String username)
	{
		return null;
	}

	@Override
	public void registerAccount(String username, String rawPassword, Role authority)
	{
	}


}
