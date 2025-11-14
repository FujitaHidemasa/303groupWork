package com.example.voidr.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.voidr.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService 
{

	private final JavaMailSender mailSender;

	@Override
	public void sendVerificationCode(String toEmail, String code) 
	{
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom("voidr.shop@gmail.com");
		msg.setTo(toEmail);
		msg.setSubject("VOIDR セキュリティコード");
		msg.setText("セキュリティコード: " + code + "\nこのコードは10分間有効です。");
		mailSender.send(msg);
	}
	
	// 11/13 追加★
	@Override
	public void sendPasswordResetPin(String toEmail, String pin) 
	{
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom("voidr.shop@gmail.com");
		msg.setTo(toEmail);
		msg.setSubject("【VOIDR】パスワード再設定用PINコード");
		msg.setText
		("パスワード再設定の確認コード（PIN）: " + pin + "\nこのコードは10分間有効です。");
		mailSender.send(msg);
	}
}
