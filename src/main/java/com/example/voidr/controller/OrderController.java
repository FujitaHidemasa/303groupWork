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
import com.example.voidr.entity.OrderList;
import com.example.voidr.service.OrderListService;
import com.example.voidr.service.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage/orders")
public class OrderController {

	private final OrderService orderService;
	private final OrderListService orderListService;

	/**
	 * 購入履歴一覧を表示
	 * ログイン中ユーザーの注文リストを取得し、購入履歴を表示する
	 */
	@GetMapping
	public String showOrderHistory(Model model, Principal principal) {
		if (principal == null) {
			// ログインしていない場合はログインページへリダイレクト
			return "redirect:/login";
		}

		String username = principal.getName();

		// ユーザーの注文リストを取得
		OrderList orderList = orderListService.findByUserName(username);

		List<Order> orders;
		if (orderList == null) {
			// 注文履歴が無い場合は空リスト
			orders = List.of();
		} else {
			// 購入履歴を取得
			orders = orderService.getOrderHistory(orderList.getId());
		}

		model.addAttribute("orders", orders);
		return "order/history"; // templates/order/history.html を参照
	}

	/**
	 * 購入確定処理（「購入する」ボタン押下時）
	 */
	@PostMapping("/confirm")
	public String confirmPurchase(@RequestParam("orderListId") long orderListId) {
		orderService.confirmPurchase(orderListId);
		return "redirect:/mypage/orders";
	}
}
