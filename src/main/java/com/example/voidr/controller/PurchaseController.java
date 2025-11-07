package com.example.voidr.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.voidr.entity.Account;
import com.example.voidr.entity.OrderItem;
import com.example.voidr.entity.OrderList;
import com.example.voidr.service.AccountService;
import com.example.voidr.service.CartService;
import com.example.voidr.service.OrderItemService;
import com.example.voidr.service.OrderListService;
import com.example.voidr.view.CartView;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/voidrshop")
@RequiredArgsConstructor
public class PurchaseController {

	private final CartService cartService;
	private final OrderListService orderListService;
	private final OrderItemService orderItemService;
	private final AccountService accountService;

	// ==============================
	// ✅ 購入画面
	// ==============================
	@GetMapping("/purchase")
	public String showPurchasePage(Model model, Principal principal) {
		String username = principal.getName();
		Account account = accountService.findByUsername(username);
		long userId = (account != null) ? account.getId() : 0L;

		List<CartView> cartList = cartService.list(userId); // CartViewにはitem情報入り

		if (cartList == null || cartList.isEmpty()) {
			model.addAttribute("cartItems", java.util.Collections.emptyList());
			model.addAttribute("totalPrice", 0);
			model.addAttribute("message", "カートに商品がありません。");
			return "shop/purchase/purchase";
		}

		int total = cartList.stream()
				.mapToInt(cv -> cv.getItem().getPrice() * cv.getCart().getQuantity())
				.sum();

		model.addAttribute("cartItems", cartList);
		model.addAttribute("totalPrice", total);
		return "shop/purchase/purchase";
	}

	// ==============================
	// ✅ 購入確認画面
	// ==============================
	@PostMapping("/purchase/confirm")
	public String confirmPurchase(
			@RequestParam("paymentMethod") String paymentMethod,
			Model model,
			Principal principal) {

		String username = principal.getName();
		Account account = accountService.findByUsername(username);
		long userId = (account != null) ? account.getId() : 0L;

		// ✅ CartViewで取得（Item情報含む）
		List<CartView> cartList = cartService.list(userId);

		if (cartList == null || cartList.isEmpty()) {
			model.addAttribute("errorMessage", "カートが空です。");
			return "redirect:/voidrshop/cart";
		}

		int totalPrice = cartList.stream()
				.mapToInt(cv -> cv.getItem().getPrice() * cv.getCart().getQuantity())
				.sum();

		model.addAttribute("cartItems", cartList);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("paymentMethod", paymentMethod);

		return "shop/purchase/purchase_confirm";
	}

	// ==============================
	// ✅ 購入完了画面
	// ==============================
	@PostMapping("/purchase/complete")
	@Transactional
	public String completePurchase(
			@RequestParam("paymentMethod") String paymentMethod,
			Principal principal,
			Model model) {

		String username = principal.getName();
		Account account = accountService.findByUsername(username);
		long userId = (account != null) ? account.getId() : 0L;

		List<CartView> cartList = cartService.list(userId);

		if (cartList == null || cartList.isEmpty()) {
			model.addAttribute("errorMessage", "カートが空です。");
			return "redirect:/voidrshop/cart";
		}

		int totalPrice = cartList.stream()
				.mapToInt(cv -> cv.getItem().getPrice() * cv.getCart().getQuantity())
				.sum();

		// ✅ 注文リスト作成
		OrderList orderList = new OrderList();
		orderList.setAccountId(userId);
		orderList.setUsername(username);
		orderList.setTotalPrice(totalPrice);
		orderList.setPaymentMethod(paymentMethod);
		orderListService.createOrderList(orderList);

		// ✅ 注文アイテム登録
		for (CartView cv : cartList) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderListId(orderList.getId());
			orderItem.setItemId(cv.getItem().getId());
			orderItem.setQuantity(cv.getCart().getQuantity());
			orderItem.setPrice(cv.getItem().getPrice());
			orderItemService.addOrderItem(orderItem);
		}

		// ✅ カートを空にする
		cartService.clearCart(username);

		model.addAttribute("orderListId", orderList.getId());
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("paymentMethod", paymentMethod);

		return "shop/purchase/purchase_complete";
	}
}
