package com.example.voidr.service;

public interface EmailService 
{
	void sendVerificationCode(String toEmail, String code);
	
	// 11/13 ★追加: パスワード再設定PIN送信用
	void sendPasswordResetPin(String toEmail, String pin);
}
