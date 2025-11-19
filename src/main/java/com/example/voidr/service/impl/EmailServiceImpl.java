package com.example.voidr.service.impl;

import java.time.format.DateTimeFormatter; // ★追加
import java.util.Locale; // ★追加

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.voidr.dto.PurchaseReceiptDto;
import com.example.voidr.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService 
{

	private final JavaMailSender mailSender;

	// 注文日時メール表示用フォーマッタ
	private static final DateTimeFormatter ORDER_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH時mm分",
			Locale.JAPAN);

	@Override
	public void sendVerificationCode(String toEmail, String code) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom("voidr.shop@gmail.com");
		msg.setTo(toEmail);
		msg.setSubject("VOIDR セキュリティコード");
		msg.setText("セキュリティコード: " + code + "\nこのコードは10分間有効です。");
		mailSender.send(msg);
	}

	// パスワード再設定用
	@Override
	public void sendPasswordResetPin(String toEmail, String pin) 
	{
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom("voidr.shop@gmail.com");
		msg.setTo(toEmail);
		msg.setSubject("【VOIDR】パスワード再設定用PINコード");
		msg.setText("パスワード再設定の確認コード（PIN）: " + pin + "\nこのコードは10分間有効です。");
		mailSender.send(msg);
	}

	// 購入完了メール送信
	@Override
	public void sendPurchaseReceipt(String toEmail, PurchaseReceiptDto dto) 
	{

		SimpleMailMessage msg = new SimpleMailMessage();
		// 表示名付き From
		msg.setFrom("VOIDR OFFICIAL STORE <voidr.shop@gmail.com>");
		msg.setTo(toEmail);

		// 件名：[VOIDR] ご購入ありがとうございます（注文番号: {番号}）
		msg.setSubject(String.format("【VOIDR】ご購入ありがとうございます（注文番号: %d）", dto.getOrderId()));

		// 本文生成
		msg.setText(buildPurchaseReceiptBody(dto));

		mailSender.send(msg);
	}

	// 購入完了メール本文テンプレ
	private String buildPurchaseReceiptBody(PurchaseReceiptDto dto) 
	{

		StringBuilder sb = new StringBuilder();

		sb.append(dto.getDisplayName()).append(" 様").append("\n\n");
		sb.append("この度は VOIDR OFFICIAL STORE をご利用いただきありがとうございます。").append("\n");
		sb.append("ご注文内容は下記の通りです。").append("\n\n");

		sb.append("[注文情報]").append("\n");
		sb.append("注文番号: ").append(dto.getOrderId()).append("\n");

		// 注文日時を「yyyy年MM月dd日 HH時mm分」形式で表示
		if (dto.getOrderDateTime() != null) {
			String formatted = dto.getOrderDateTime().format(ORDER_DATETIME_FORMATTER);
			sb.append("注文日時: ").append(formatted).append("\n");
		}

		if (dto.getPaymentMethod() != null) {
			sb.append("支払い方法: ").append(dto.getPaymentMethod()).append("\n");
		}
		sb.append("\n");

		sb.append("[お支払い金額]").append("\n");
		sb.append("商品小計: ").append(dto.getSubtotal()).append("円").append("\n");
		sb.append("送料: ").append(dto.getShippingFee()).append("円").append("\n");
		sb.append("合計: ").append(dto.getFinalTotal()).append("円").append("\n\n");

		sb.append("[ご注文商品]").append("\n");
		if (dto.getItems() != null) {
			for (PurchaseReceiptDto.Item item : dto.getItems()) {
				sb.append("- ")
						.append(item.getItemName())
						.append(" / ")
						.append(item.getUnitPrice()).append("円")
						.append(" × ")
						.append(item.getQuantity())
						.append(" = ")
						.append(item.getSubtotal()).append("円")
						.append("\n");
			}
		}
		sb.append("\n");

		sb.append("[配送先]").append("\n");
		if (dto.getAddress() != null) {
			sb.append(dto.getAddress()).append("\n");
		}
		if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
			sb.append("電話番号: ").append(dto.getPhoneNumber()).append("\n");
		}
		sb.append("\n");

		sb.append("お問い合わせは、サイト内のお問い合わせフォーム（/voidr/contact）よりご連絡ください。").append("\n\n");
		sb.append("VOIDR OFFICIAL STORE").append("\n");

		return sb.toString();
	}
}
