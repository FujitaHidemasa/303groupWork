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
}
