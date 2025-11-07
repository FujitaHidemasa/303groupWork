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
import com.example.voidr.entity.Cart;
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

		List<CartView> cartList = cartService.list(userId);

		if (cartList == null || cartList.isEmpty()) {
			model.addAttribute("cartList", java.util.Collections.emptyList());
			model.addAttribute("total", 0);
			model.addAttribute("message", "カートに商品がありません。");
			return "shop/purchase/purchase";
		}

		// ✅ 合計金額計算（CartViewにgetSubtotal()がある前提）
		int total = cartList.stream()
				.mapToInt(cv -> {
					try {
						return cv.getSubtotal(); // CartViewにsubtotalがある場合
					} catch (Exception e) {
						// ない場合は quantity × price で計算
						if (cv.getCart() != null && cv.getItem() != null) {
							return cv.getCart().getQuantity() * cv.getItem().getPrice();
						}
						return 0;
					}
				})
				.sum();

		model.addAttribute("cartList", cartList);
		model.addAttribute("total", total);
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
		List<Cart> cartItems = cartService.findByUsername(username);

		if (cartItems == null || cartItems.isEmpty()) {
			model.addAttribute("errorMessage", "カートが空です。");
			return "redirect:/voidrshop/cart";
		}

		int totalPrice = cartItems.stream()
				.mapToInt(c -> c.getItem().getPrice() * c.getQuantity())
				.sum();

		model.addAttribute("cartItems", cartItems);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("paymentMethod", paymentMethod);

		return "shop/purchase/purchase_confirm";
	}

	// ==============================
	// ✅ 購入完了画面（購入履歴登録）
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

		List<Cart> cartItems = cartService.findByUsername(username);

		if (cartItems == null || cartItems.isEmpty()) {
			model.addAttribute("errorMessage", "カートが空です。");
			return "redirect:/voidrshop/cart";
		}

		int totalPrice = cartItems.stream()
				.mapToInt(c -> c.getItem().getPrice() * c.getQuantity())
				.sum();

		// ✅ 注文情報（OrderList）登録
		OrderList orderList = new OrderList();
		orderList.setAccountId(userId);
		orderList.setUsername(username);
		orderList.setTotalPrice(totalPrice);
		orderList.setPaymentMethod(paymentMethod);
		orderListService.createOrderList(orderList);

		// ✅ 各商品を注文アイテム（OrderItem）に登録
		for (Cart cart : cartItems) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderListId(orderList.getId());
			orderItem.setItemId(cart.getItem().getId());
			orderItem.setQuantity(cart.getQuantity());
			orderItem.setPrice(cart.getItem().getPrice());
			orderItemService.addOrderItem(orderItem);
		}

		// ✅ カートを空にする
		cartService.clearCart(username);

		// ✅ 完了画面用データ設定
		model.addAttribute("orderListId", orderList.getId());
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("paymentMethod", paymentMethod);

		return "shop/purchase/purchase_complete";
	}
}
