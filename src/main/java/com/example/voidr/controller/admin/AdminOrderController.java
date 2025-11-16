package com.example.voidr.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.voidr.entity.Order;
import com.example.voidr.entity.OrderList;
import com.example.voidr.service.OrderListService;
import com.example.voidr.service.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
	
	// 注文リスト用サービス
	private final OrderListService orderListService;
	// 注文詳細用サービス
	private final OrderService orderService;

	/** 注文一覧（管理画面トップ） */
	@GetMapping
	public String showOrders(Model model) {

		// 全ユーザー分の注文リストを新しい順に取得
		List<OrderList> orderLists = orderListService.getAllOrderListsWithUser();

		// 注文ごとの合計金額（商品価格×数量の合計）を計算
		Map<Long, Integer> totalPriceMap = new HashMap<>();
		for (OrderList ol : orderLists) {
			List<Order> orders = orderService.getOrderHistory(ol.getId());
			int total = 0;
			for (Order order : orders) {
				total += order.getPrice() * order.getQuantity();
			}
			totalPriceMap.put(ol.getId(), total);
		}

		model.addAttribute("orderLists", orderLists);
		model.addAttribute("totalPriceMap", totalPriceMap);
		model.addAttribute("pageTitle", "注文管理");
		return "admin/orders";
	}
	
	/** 注文詳細（1件分・商品一覧） */
	@GetMapping("/{orderListId}")
	public String showOrderDetail(@PathVariable long orderListId, Model model) {

		// 該当注文の明細（商品ごとの情報）を取得
		List<Order> orders = orderService.getOrderHistory(orderListId);
		if (orders == null || orders.isEmpty()) {
			// 本来はフラッシュメッセージなどにした方が良いが、まずは一覧に戻す
			return "redirect:/admin/orders";
		}

		// 合計金額を計算
		int total = 0;
		for (Order order : orders) {
			total += order.getPrice() * order.getQuantity();
		}

		// 管理画面用に、ユーザー名や注文日時も表示したいので OrderList を取得
		List<OrderList> allOrderLists = orderListService.getAllOrderListsWithUser();
		OrderList target = null;
		for (OrderList ol : allOrderLists) {
			if (ol.getId() == orderListId) {
				target = ol;
				break;
			}
		}

		model.addAttribute("orders", orders);
		model.addAttribute("orderListId", orderListId);
		model.addAttribute("totalPrice", total);

		if (target != null) {
			// 会員ID（ログインID）
			model.addAttribute("orderUserName", target.getUsername());
			// 注文日時
			model.addAttribute("orderCreatedAt", target.getCreatedAt());
			// ここから購入画面で入力された内容
			model.addAttribute("paymentMethod", target.getPaymentMethod());
			model.addAttribute("address", target.getAddress());
			model.addAttribute("deliveryDate", target.getDeliveryDate());
			model.addAttribute("deliveryTime", target.getDeliveryTime());
			// ステータス（NEW / SHIPPED など）
			model.addAttribute("status", target.getStatus());
		}

		model.addAttribute("pageTitle", "注文詳細");

		return "admin/order_detail";
	}
	
	// ステータス更新（NEW / SHIPPED など）
	@PostMapping("/{orderListId}/status")
	public String updateStatus(
			@PathVariable("orderListId") long orderListId,
			@RequestParam("status") String status,
			RedirectAttributes redirectAttributes) {

		orderListService.updateStatus(orderListId, status);
		redirectAttributes.addFlashAttribute(
				"successMessage",
				"注文ID " + orderListId + " のステータスを更新しました。");

		return "redirect:/admin/orders";
	}

}
