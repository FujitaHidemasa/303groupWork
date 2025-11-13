package com.example.voidr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.voidr.service.PasswordResetService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordResetController 
{

	private final PasswordResetService service;

	@Data
	public static class ForgotForm 
	{
		private String email;
	}

	@Data
	public static class ResetForm 
	{
		private String email;
		private String pin;
		private String newPassword;
		private String confirmPassword;
	}

	// メール入力フォーム
	@GetMapping("/forgot")
	public String forgot(Model model) 
	{
		model.addAttribute("form", new ForgotForm());
		return "shop/forgot_password";
	}

	// PIN発行
	@PostMapping("/forgot/send")
	public String send(@ModelAttribute("form") ForgotForm form, Model model) 
	{
		try {
			service.issuePin(form.getEmail());
			ResetForm rf = new ResetForm();
			rf.setEmail(form.getEmail());
			model.addAttribute("form", rf);
			return "shop/reset_password";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "shop/forgot_password";
		}
	}

	// 検証 + 再設定
	@PostMapping("/reset/submit")
	public String submit(@ModelAttribute("form") ResetForm form, Model model) 
	{
		try {
			if (form.getNewPassword() == null || !form.getNewPassword().equals(form.getConfirmPassword())) {
				model.addAttribute("error", "パスワードが一致しません。");
				return "shop/reset_password";
			}
			service.verifyAndReset(form.getEmail(), form.getPin(), form.getNewPassword());
			return "shop/reset_done";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "shop/reset_password";
		}
	}
}
