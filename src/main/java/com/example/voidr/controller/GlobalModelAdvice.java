package com.example.voidr.controller;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.voidr.entity.Account;
import com.example.voidr.service.AccountService;
import com.example.voidr.service.CartService;

import lombok.RequiredArgsConstructor;

/** ★追加：ヘッダのカートバッジ用に常に cartCount を注入 */
@ControllerAdvice(annotations = Controller.class)
@RequiredArgsConstructor
public class GlobalModelAdvice 
{
	private final AccountService accountService;
	private final CartService cartService;

	@ModelAttribute("cartCount")
	public Integer cartCount(Authentication auth, Principal principal) 
	{
		if (auth == null || !auth.isAuthenticated() || principal == null) {
			return 0;
		}
		Account acc = accountService.findByUsername(principal.getName());
		if (acc == null)
			return 0;
		return cartService.countInBadge(acc.getId());
	}
}
