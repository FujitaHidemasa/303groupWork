package com.example.voidr.service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.voidr.entity.Account;
import com.example.voidr.entity.PasswordResetPin;
import com.example.voidr.repository.AccountMapper;
import com.example.voidr.repository.PasswordResetPinMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

	private final PasswordResetPinMapper pinMapper;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;
	private final AccountMapper accountMapper;

	// ★PIN発行（10分）
	@Transactional
	public void issuePin(String rawEmail) 
	{
		String email = normalizeEmail(rawEmail);

		Account acc = accountMapper.findByEmail(email);
		if (acc == null) {
			// セキュリティ上は「メールを送信しました」でごまかす選択肢もある
			throw new IllegalArgumentException("このメールアドレスは登録されていません。");
		}

		// 直近10分で3回まで
		if (pinMapper.countIssuedRecently(email, 10) >= 3) {
			throw new IllegalStateException("短時間に多数のリクエストが行われました。時間をおいて再実行してください。");
		}

		String pin = generate6Digits();
		PasswordResetPin rec = new PasswordResetPin();
		rec.setEmail(email);
		rec.setPin(pin);
		rec.setExpireAt(LocalDateTime.now().plusMinutes(10));
		pinMapper.insert(rec);

		emailService.sendPasswordResetPin(email, pin);
	}
	
	// ★追加
	private boolean isValidPassword(String s) {
		if (s == null)
			return false;
		if (s.length() < 8 || s.length() > 20)
			return false;
		return s.matches("^[A-Za-z0-9]+$");
	}

	// ★検証＋更新
	@Transactional
	public void verifyAndReset(String rawEmail, String rawPin, String newPassword)
	{
		// 統一ポリシーを強制（英数字8〜20）
		if (!isValidPassword(newPassword)) {
			throw new IllegalArgumentException("パスワードは英数字8〜20文字で入力してください。");
		}
	    
		String email = normalizeEmail(rawEmail);
		String pin = normalizeDigits(rawPin);

		PasswordResetPin active = pinMapper.findActive(email, pin);
		if (active == null) {
			throw new IllegalArgumentException("PINが不正、または有効期限切れです。");
		}

		// 一度使い切り
		pinMapper.consume(active.getId());

		String enc = passwordEncoder.encode(newPassword);
		accountMapper.updatePasswordByEmail(email, enc);
	}

	// ===== util =====
	private String normalizeEmail(String s) 
	{
		if (s == null)
			return null;
		return Normalizer.normalize(s, Normalizer.Form.NFKC).trim();
	}

	private String normalizeDigits(String s) 
	{
		if (s == null)
			return null;
		String nfkc = Normalizer.normalize(s, Normalizer.Form.NFKC);
		return nfkc.replaceAll("[^0-9]", "");
	}

	private String generate6Digits() 
	{
		return String.format("%06d", new Random().nextInt(1_000_000));
	}
}
