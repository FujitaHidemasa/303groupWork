package com.example.voidr.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.voidr.entity.Account;
import com.example.voidr.service.AccountService;

import lombok.RequiredArgsConstructor;

/**
 * 会員管理画面のコントローラー
 */

@Controller
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {
	
	private final AccountService accountService;

    @GetMapping
    public String showMembers(@RequestParam(name = "keyword", required = false)String keyword,
            Model model) {
    	
    	List<Account> members;

        if (keyword == null || keyword.isBlank()) {
            members = accountService.findAll();       // 全件
        } else {
            members = accountService.searchMembers(keyword); // 部分一致検索
        }

        model.addAttribute("members", members);
        model.addAttribute("keyword", keyword);
    	
        model.addAttribute("pageTitle", "会員管理");
        return "admin/members"; // templates/admin/members.html
    }
}
