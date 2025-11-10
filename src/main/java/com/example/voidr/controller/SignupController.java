package com.example.voidr.controller;

import java.text.Normalizer;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
// ★追加
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.voidr.entity.Role;
import com.example.voidr.form.SignupForm;
import com.example.voidr.service.AccountService;
import com.example.voidr.service.EmailService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignupController {

	private final AccountService accountService;
	private final EmailService emailService;
	private final AuthenticationManager authenticationManager;

	// セッションへの保存キー
	private static final String PENDING_SIGNUP_KEY = "PENDING_SIGNUP";

	// セッション保持用レコード
	static class PendingSignup {
		SignupForm form;
		String code;
		Instant expiresAt;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// 前後空白をトリム（空文字→null）
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	// ===== Step0: サインアップ画面表示 =====
	@GetMapping
	public String getSignup(
			@ModelAttribute("form") SignupForm form) // ★追加：Thymeleafの th:object="${form}" と一致させる
	{
		return "shop/signup";
	}

	// ===== Step1: フォーム送信 → 6桁コード生成＆送信 → 確認画面へ =====
	@PostMapping
	public String postSignup(
			@Valid @ModelAttribute("form") SignupForm form, // ★修正：@ModelAttribute("form") を付与
			BindingResult binding,
			Model model,
			HttpSession session) {

		if (binding.hasErrors()) {
			return "shop/signup";
		}

		// 6桁コード生成
		String code = String.format("%06d", new Random().nextInt(1_000_000));
		Instant expires = Instant.now().plus(Duration.ofMinutes(10));

		PendingSignup pending = new PendingSignup();
		pending.form = form;
		pending.code = code;
		pending.expiresAt = expires;

		session.setAttribute(PENDING_SIGNUP_KEY, pending);

		// メール送信（環境設定は application.properties にて）
		emailService.sendVerificationCode(form.getEmail(), code);

		model.addAttribute("email", form.getEmail());
		return "shop/signup_verify";
	}

	// ===== Step2: コード入力画面（直アクセス時のガード） =====
	// ★修正：Model を受け取り email を積む
	@GetMapping("/verify")
	public String getVerify(HttpSession session, Model model) { // ★修正
		PendingSignup p = (PendingSignup) session.getAttribute(PENDING_SIGNUP_KEY); // ★追加
		if (p == null) {
			return "redirect:/signup";
		}
		model.addAttribute("email", p.form.getEmail()); // ★追加：テンプレ表示用
		return "shop/signup_verify"; // ★修正：そのまま表示
	}

	// ===== Step2: コード検証 → 登録 → 自動ログイン =====
	@PostMapping("/verify")
	public String postVerify(
			String code, // <input name="code">
			Model model,
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session) {
		// ★追加（ここから）
		code = (code == null) ? ""
				: Normalizer.normalize(code, Normalizer.Form.NFKC) // 全角→半角
						.replaceAll("[^0-9]", ""); // 数字以外を除去
		// ★追加（ここまで）

		PendingSignup pending = (PendingSignup) session.getAttribute(PENDING_SIGNUP_KEY);
		if (pending == null) {
			return "redirect:/signup";
		}

		// 期限切れ
		if (Instant.now().isAfter(pending.expiresAt)) {
			session.removeAttribute(PENDING_SIGNUP_KEY);
			model.addAttribute("error", "セキュリティコードの有効期限が切れました。最初からやり直してください。");
			return "shop/signup";
		}

		// 不一致
		if (pending.code == null || !pending.code.equals(code)) {
			model.addAttribute("email", pending.form.getEmail());
			model.addAttribute("error", "セキュリティコードが正しくありません。");
			return "shop/signup_verify";
		}

		// === 登録（Role.USER 固定） ===
		SignupForm f = pending.form;
		accountService.registerAccount(
				f.getUsername(),
				f.getPassword(),
				Role.USER,
				f.getDisplayName(),
				f.getEmail(),
				f.getAddress(),
				f.getPhoneNumber());

		// 一時情報 破棄
		session.removeAttribute(PENDING_SIGNUP_KEY);

		// === 自動ログイン ===
		UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(f.getUsername(),
				f.getPassword());
		Authentication authResult = authenticationManager.authenticate(authReq);

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authResult);
		SecurityContextHolder.setContext(context);

		// 明示的にセッションへ保存
		new HttpSessionSecurityContextRepository().saveContext(context, request, response);

		return "redirect:/voidrshop";
	}
}
