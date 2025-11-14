package com.example.voidr.controller;

import java.security.Principal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
	 * 購入履歴一覧を表示（並び替え対応）
	 */
	@GetMapping
	public String showOrderHistory(
			Model model,
			Principal principal,
			@RequestParam(name = "sort", defaultValue = "desc") String sort) {

		if (principal == null) {
			return "redirect:/login";
		}

		String username = principal.getName();
		List<OrderList> orderLists = orderListService.findByUserName(username);

		// 並び替え（desc: 新しい順, asc: 古い順）
		if (sort.equals("asc")) {
			orderLists.sort(Comparator.comparing(OrderList::getCreatedAt));
		} else {
			orderLists.sort(Comparator.comparing(OrderList::getCreatedAt).reversed());
		}

		// 注文リストIDごとに注文詳細をまとめる
		Map<Long, List<Order>> groupedOrders = new LinkedHashMap<>();
		Map<Long, Integer> totalPriceMap = new LinkedHashMap<>();
		Map<Long, Integer> shippingFeeMap = new LinkedHashMap<>();

		for (OrderList ol : orderLists) {
			List<Order> orderHistory = orderService.getOrderHistory(ol.getId());
			groupedOrders.put(ol.getId(), orderHistory);

			// 商品合計（数量考慮）
			int total = orderHistory.stream()
					.mapToInt(order -> order.getPrice() * order.getQuantity())
					.sum();

			// 送料（5000円以上で無料）
			int shippingFee = total >= 5000 ? 0 : 500;

			shippingFeeMap.put(ol.getId(), shippingFee);
			totalPriceMap.put(ol.getId(), total + shippingFee);
		}

		model.addAttribute("groupedOrders", groupedOrders);
		model.addAttribute("totalPriceMap", totalPriceMap);
		model.addAttribute("shippingFeeMap", shippingFeeMap);
		model.addAttribute("sort", sort);

		return "order/history";
	}
}
