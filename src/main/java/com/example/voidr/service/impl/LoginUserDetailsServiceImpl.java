package com.example.voidr.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.voidr.entity.Account;
import com.example.voidr.entity.LoginUser;
import com.example.voidr.repository.AccountMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginUserDetailsServiceImpl implements UserDetailsService {

    private final AccountMapper accountMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountMapper.findByUsername(username);
        if(account == null)
        {
        	new UsernameNotFoundException("User not found: " + username);
        }
        System.out.println(account.getDisplayName());

        return new LoginUser(account); // ← ここで正しいAccountを渡す！
    }
}

