package com.example.voidr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/voidrshop")
public class PurchaseController {

	// 購入画面
	@GetMapping("/purchase")
	public String showPurchase() {
		return "shop/purchase/purchase";
	}

	// 購入確認画面
	@GetMapping("/purchase_confirm")
	public String showPurchaseConfirm() {
		return "shop/purchase/purchase_confirm";
	}

	// 購入完了画面
	@GetMapping("/purchase_complete")
	public String showPurchaseComplete() {
		return "shop/purchase/purchase_complete";
	}
}
