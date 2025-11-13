package com.example.voidr.controller;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	 */
	// OrderController.java 内
	@GetMapping
	public String showOrderHistory(Model model, Principal principal) {
		if (principal == null) {
			return "redirect:/login";
		}

		String username = principal.getName();
		List<OrderList> orderLists = orderListService.findByUserName(username);

		// Map<Long, List<Order>> を作成（注文リストIDごとにまとめる）
		Map<Long, List<Order>> groupedOrders = new LinkedHashMap<>();
		Map<Long, Integer> totalPriceMap = new LinkedHashMap<>();

		for (OrderList ol : orderLists) {
			List<Order> orderHistory = orderService.getOrderHistory(ol.getId());
			groupedOrders.put(ol.getId(), orderHistory);

			// 注文ごとの合計金額を計算
			int total = orderHistory.stream()
					.mapToInt(Order::getPrice)
					.sum();
			totalPriceMap.put(ol.getId(), total);
		}

		model.addAttribute("groupedOrders", groupedOrders);
		model.addAttribute("totalPriceMap", totalPriceMap);

		return "order/history";
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
