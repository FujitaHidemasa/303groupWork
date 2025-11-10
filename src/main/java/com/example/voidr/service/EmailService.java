package com.example.voidr.service;

public interface EmailService 
{
	void sendVerificationCode(String toEmail, String code);
}
