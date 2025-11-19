package com.example.voidr.controller;

import java.security.Principal;
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

	@GetMapping
	public String showOrderHistory(Model model,
			Principal principal,
			@RequestParam(name = "month", required = false) String targetMonth,
			@RequestParam(name = "historyKeyword", required = false) String keyword) {

		if (principal == null)
			return "redirect:/login";

		String username = principal.getName();
		List<OrderList> orderLists = orderListService.findByUserName(username);

		// 月ごとのフィルター
		Map<Long, List<Order>> groupedOrders = new LinkedHashMap<>();
		Map<Long, Integer> totalPriceMap = new LinkedHashMap<>();
		Map<Long, Integer> shippingFeeMap = new LinkedHashMap<>();
		Map<Long, String> statusMap = new LinkedHashMap<>();
		Map<Long, String> deliveryMap = new LinkedHashMap<>();

		for (OrderList ol : orderLists) {
			// 作成日を yyyy-MM に変換
			String orderMonth = ol.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));

			// 月フィルターがあればスキップ
			if (targetMonth != null && !targetMonth.isEmpty() && !targetMonth.equals(orderMonth)) {
				continue;
			}

			List<Order> orderHistory = orderService.getOrderHistory(ol.getId());

			if (keyword != null && !keyword.trim().isEmpty()) {
				String lower = keyword.toLowerCase();
				orderHistory = orderHistory.stream()
						.filter(o -> o.getItemName().toLowerCase().contains(lower))
						.toList();
			}

			if (orderHistory.isEmpty())
				continue;

			groupedOrders.put(ol.getId(), orderHistory);

			int total = orderHistory.stream()
					.mapToInt(order -> order.getPrice() * order.getQuantity())
					.sum();

			int shippingFee = (total >= 5000) ? 0 : 500;
			shippingFeeMap.put(ol.getId(), shippingFee);
			totalPriceMap.put(ol.getId(), total + shippingFee);
			statusMap.put(ol.getId(), ol.getStatus());

			String deliveryText = "";
			if (ol.getDeliveryDate() != null)
				deliveryText += ol.getDeliveryDate();
			if (ol.getDeliveryTime() != null)
				deliveryText += " " + ol.getDeliveryTime();
			deliveryMap.put(ol.getId(), deliveryText.trim());
		}

		// 月リスト作成（ドロップダウン用）
		java.util.Set<String> availableMonths = orderLists.stream()
				.map(ol -> ol.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")))
				.collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));

		model.addAttribute("groupedOrders", groupedOrders);
		model.addAttribute("totalPriceMap", totalPriceMap);
		model.addAttribute("shippingFeeMap", shippingFeeMap);
		model.addAttribute("statusMap", statusMap);
		model.addAttribute("deliveryMap", deliveryMap);
		model.addAttribute("availableMonths", availableMonths);
		model.addAttribute("targetMonth", targetMonth);
		model.addAttribute("historyKeyword", keyword);

		return "order/history";
	}

	@PostMapping("/reorder")
	public String reorderSingleItem(@RequestParam("itemId") Long itemId,
			@RequestParam(name = "quantity", defaultValue = "1") Integer quantity,
			@AuthenticationPrincipal LoginUser loginUser,
			RedirectAttributes redirectAttributes) {

		if (loginUser == null)
			return "redirect:/login";

		Long userId = loginUser.getId();
		Item item = itemService.getItemById(itemId);

		if (item == null || Boolean.TRUE.equals(item.getIsDeleted())) {
			redirectAttributes.addFlashAttribute("errorMessage",
					"この商品は現在販売を終了しているため、再購入できません。");
			return "redirect:/mypage/orders";
		}

		cartService.addItem(userId, itemId, quantity);
		redirectAttributes.addFlashAttribute("successMessage", "商品をカートに追加しました。");

		return "redirect:/voidrshop/cart";
	}

	// ★ 修正版: 注文単位キャンセル
	@PostMapping("/cancel")
	public String cancelOrder(@RequestParam("orderListId") Long orderListId,
			@AuthenticationPrincipal LoginUser loginUser,
			RedirectAttributes redirectAttributes) {

		if (loginUser == null)
			return "redirect:/login";

		try {
			// 注文リスト単位のキャンセル処理を呼び出す
			orderService.cancelOrder(orderListId, loginUser.getUsername());
			redirectAttributes.addFlashAttribute("successMessage", "注文をキャンセルしました。");
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}

		return "redirect:/mypage/orders";
	}

}
