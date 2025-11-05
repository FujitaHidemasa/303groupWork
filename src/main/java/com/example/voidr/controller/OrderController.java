package com.example.voidr.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.voidr.entity.Order;
import com.example.voidr.entity.OrderList; // 👈 追加
import com.example.voidr.service.OrderListService; // 👈 追加：OrderList取得用サービス（例）
import com.example.voidr.service.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage/orders")
public class OrderController {

	private final OrderService orderService;
	private final OrderListService orderListService; // 👈 追加

	/**
	 * ✅ 購入履歴一覧を表示
	 */
	@GetMapping
	public String showOrderHistory(Model model, Principal principal) {
		// ログイン中ユーザーを取得
		String username = principal.getName();

		// ユーザーの注文リストIDを取得（例：1ユーザー1つのOrderListを持つ想定）
		OrderList orderList = orderListService.findByUserName(username);
		if (orderList == null) {
			model.addAttribute("orders", List.of());
			return "order/history";
		}

		// 購入履歴取得
		List<Order> orders = orderService.getOrderHistory(orderList.getId());
		model.addAttribute("orders", orders);

		return "order_history";
	}

	/**
	 * ✅ 購入確定処理（「購入する」ボタン押下時など）
	 */
	@PostMapping("/confirm")
	public String confirmPurchase(@RequestParam("orderListId") long orderListId) {
		orderService.confirmPurchase(orderListId);
		return "redirect:/mypage/orders";
	}
}
