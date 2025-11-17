package com.example.voidr.controller;

import java.security.Principal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.voidr.entity.Item;
import com.example.voidr.entity.LoginUser;
import com.example.voidr.entity.Order;
import com.example.voidr.entity.OrderList;
import com.example.voidr.service.CartService;
import com.example.voidr.service.ItemService;
import com.example.voidr.service.OrderListService;
import com.example.voidr.service.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage/orders")
public class OrderController {

	private final OrderService orderService;
	private final OrderListService orderListService;
	private final ItemService itemService;
	private final CartService cartService;

	/**
	 * 購入履歴一覧を表示（並び替え対応）
	 */
	@GetMapping
	public String showOrderHistory(
			Model model,
			Principal principal,
			@RequestParam(name = "sort", defaultValue = "desc") String sort,
			@RequestParam(name = "historyKeyword", required = false) String keyword) {

		if (principal == null) {
			return "redirect:/login";
		}

		String username = principal.getName();
		List<OrderList> orderLists = orderListService.findByUserName(username);

		// ▼ 並び替え
		if (sort.equals("asc")) {
			orderLists.sort(Comparator.comparing(OrderList::getCreatedAt));
		} else {
			orderLists.sort(Comparator.comparing(OrderList::getCreatedAt).reversed());
		}

		// ▼ 注文リストごとに注文詳細をまとめる
		Map<Long, List<Order>> groupedOrders = new LinkedHashMap<>();
		Map<Long, Integer> totalPriceMap = new LinkedHashMap<>();
		Map<Long, Integer> shippingFeeMap = new LinkedHashMap<>();
		Map<Long, String> statusMap = new LinkedHashMap<>();

		// ★追加：OrderList をそのまま保持する map（配達予定日などで使用）
		Map<Long, OrderList> orderListMap = new LinkedHashMap<>();

		for (OrderList ol : orderLists) {

			// ★追加：orderListMap に登録
			orderListMap.put(ol.getId(), ol);

			List<Order> orderHistory = orderService.getOrderHistory(ol.getId());

			// 検索キーワードフィルタ
			if (keyword != null && !keyword.trim().isEmpty()) {
				String lower = keyword.toLowerCase();
				orderHistory = orderHistory.stream()
						.filter(o -> o.getItemName().toLowerCase().contains(lower))
						.toList();
			}

			// 商品が0件ならスキップ
			if (orderHistory.isEmpty()) {
				continue;
			}

			groupedOrders.put(ol.getId(), orderHistory);

			// 合計金額
			int total = orderHistory.stream()
					.mapToInt(order -> order.getPrice() * order.getQuantity())
					.sum();

			// 送料
			int shippingFee = (total >= 5000) ? 0 : 500;

			shippingFeeMap.put(ol.getId(), shippingFee);
			totalPriceMap.put(ol.getId(), total + shippingFee);
			statusMap.put(ol.getId(), ol.getStatus());
		}

		model.addAttribute("groupedOrders", groupedOrders);
		model.addAttribute("totalPriceMap", totalPriceMap);
		model.addAttribute("shippingFeeMap", shippingFeeMap);
		model.addAttribute("statusMap", statusMap);

		// ★追加：ビューで配達予定日を表示するために必要
		model.addAttribute("orderListMap", orderListMap);

		model.addAttribute("sort", sort);
		model.addAttribute("historyKeyword", keyword);

		return "order/history";
	}

	/**
	 * ★再購入処理（1商品のみ）
	 */
	@PostMapping("/reorder")
	public String reorderSingleItem(
			@RequestParam("itemId") Long itemId,
			@RequestParam(name = "quantity", defaultValue = "1") Integer quantity,
			@AuthenticationPrincipal LoginUser loginUser,
			RedirectAttributes redirectAttributes) {

		if (loginUser == null) {
			return "redirect:/login";
		}

		Long userId = loginUser.getId();

		Item item = itemService.getItemById(itemId);

		if (item == null || Boolean.TRUE.equals(item.getIsDeleted())) {
			redirectAttributes.addFlashAttribute(
					"errorMessage",
					"この商品は現在販売を終了しているため、再購入できません。");
			return "redirect:/mypage/orders";
		}

		cartService.addItem(userId, itemId, quantity);

		redirectAttributes.addFlashAttribute(
				"successMessage",
				"商品をカートに追加しました。");

		return "redirect:/voidrshop/cart";
	}
}
