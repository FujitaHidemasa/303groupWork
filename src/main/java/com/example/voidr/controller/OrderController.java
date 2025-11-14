package com.example.voidr.controller;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

		// 注文リストIDごとに注文詳細をまとめる
		Map<Long, List<Order>> groupedOrders = new LinkedHashMap<>();
		Map<Long, Integer> totalPriceMap = new LinkedHashMap<>();
		Map<Long, Integer> shippingFeeMap = new LinkedHashMap<>();

		for (OrderList ol : orderLists) {
			List<Order> orderHistory = orderService.getOrderHistory(ol.getId()); // List<Order> で取得
			groupedOrders.put(ol.getId(), orderHistory);

			// 商品合計（数量を考慮）
			int total = orderHistory.stream()
					.mapToInt(order -> order.getPrice() * order.getQuantity()) // ← ここを修正
					.sum();

			// 送料計算：商品合計が5000円未満なら500円、それ以上なら0円
			int shippingFee = total >= 5000 ? 0 : 500;
			shippingFeeMap.put(ol.getId(), shippingFee);

			totalPriceMap.put(ol.getId(), total + shippingFee);
		}

		model.addAttribute("groupedOrders", groupedOrders);
		model.addAttribute("totalPriceMap", totalPriceMap);
		model.addAttribute("shippingFeeMap", shippingFeeMap);

		return "order/history";
	}
}