package com.example.voidr.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 購入完了メール用 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseReceiptDto 
{

	// 購入者情報
	private String displayName;
	private String address;
	private String phoneNumber;

	// 注文情報
	private long orderId;
	private LocalDateTime orderDateTime;
	private String paymentMethod;

	// 金額情報
	private int subtotal;
	private int shippingFee;
	private int finalTotal;

	// 商品明細
	private List<Item> items;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Item {
		private String itemName;
		private int unitPrice;
		private int quantity;
		private int subtotal;
	}
}
