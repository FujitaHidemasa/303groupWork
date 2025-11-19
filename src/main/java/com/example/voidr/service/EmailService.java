package com.example.voidr.service;

import com.example.voidr.dto.PurchaseReceiptDto;

public interface EmailService 
{
	void sendVerificationCode(String toEmail, String code);
	
	// パスワード再設定PIN送信用
	void sendPasswordResetPin(String toEmail, String pin);
	
	// 購入完了メール送信用
	void sendPurchaseReceipt(String toEmail, PurchaseReceiptDto dto);
}
