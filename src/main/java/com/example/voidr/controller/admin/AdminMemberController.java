package com.example.voidr.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.voidr.entity.Account;
import com.example.voidr.service.AccountService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {
	
	private final AccountService accountService;

	@GetMapping
	public String showMembers(
	        @RequestParam(name = "keyword", required = false) String keyword,
	        @RequestParam(name = "status", required = false, defaultValue = "all") String status,
	        Model model) {

	    List<Account> members;

	    // 1) 検索ワード優先
	    if (keyword != null && !keyword.isBlank()) {
	        members = accountService.searchMembers(keyword);
	    } else {
	        // 2) タブのステータスによるフィルタ
	        if (status.equals("active")) {
	            members = accountService.findActive(); // enabled = true
	        } else if (status.equals("disabled")) {
	            members = accountService.findDisabled(); // enabled = false
	        } else {
	            members = accountService.findAll(); // 全件
	        }
	    }

	    model.addAttribute("members", members);
	    model.addAttribute("keyword", keyword);
	    model.addAttribute("status", status);

	    return "admin/members";
	}

    
	// ==========================
	// 会員削除（論理削除）
	// ==========================
	@PostMapping("/delete")
	public String deleteMember(
			@RequestParam("username") String username,
			RedirectAttributes redirectAttributes) {

		accountService.deleteAccountByUsername(username);

		redirectAttributes.addFlashAttribute(
				"successMessage",
				"ユーザー「" + username + "」を退会扱いにしました。");

		return "redirect:/admin/members";
	}
	
	// ==========================
	// 権限変更（USER ⇄ ADMIN）
	// ==========================
	@PostMapping("/changeAuthority")
	public String changeAuthority(
	        @RequestParam("username") String username,
	        @RequestParam("authority") String authority,
	        RedirectAttributes redirectAttributes) {

	    accountService.updateAuthority(username, authority);

	    redirectAttributes.addFlashAttribute(
	        "successMessage",
	        "ユーザー「" + username + "」の権限を「" + authority + "」に変更しました。"
	    );

	    return "redirect:/admin/members";
	}
}
