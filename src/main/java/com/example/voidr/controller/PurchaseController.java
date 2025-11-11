package com.example.voidr.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.voidr.entity.Account;
import com.example.voidr.entity.Order;
import com.example.voidr.entity.OrderItem;
import com.example.voidr.entity.OrderList;
import com.example.voidr.service.AccountService;
import com.example.voidr.service.CartService;
import com.example.voidr.service.OrderItemService;
import com.example.voidr.service.OrderListService;
import com.example.voidr.service.OrderService;
import com.example.voidr.view.CartView;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/voidrshop/purchase")
@RequiredArgsConstructor
@Validated
@PreAuthorize("isAuthenticated()")
public class PurchaseController {

	private final CartService cartService;
	private final OrderListService orderListService;
	private final OrderService orderService;
	private final OrderItemService orderItemService;
	private final AccountService accountService;

	/** ログイン中ユーザー情報取得 */
	private Account currentUser(Principal principal) {
		return accountService.findByUsername(principal.getName());
	}

	/** 購入画面表示 */
	@GetMapping
	public String showPurchasePage(Model model, Principal principal) {
		Account account = currentUser(principal);
		if (account == null) {
			return "redirect:/login";
		}

		List<CartView> cartList = cartService.list(account.getId());
		if (cartList == null || cartList.isEmpty()) {
			model.addAttribute("cartItems", Collections.emptyList());
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

	/** 商品詳細ページから直接購入ボタン */
	@PostMapping
	public String addItemAndRedirectToPurchase(
			@RequestParam("itemId") Long itemId,
			@RequestParam(name = "quantity", defaultValue = "1") int quantity,
			Principal principal) {

		if (principal != null) {
			cartService.addItem(principal.getName(), itemId, quantity);
		}
		return "redirect:/voidrshop/purchase";
	}

	/** 購入確認画面 */
	@PostMapping("/confirm")
	public String confirmPurchase(
			@RequestParam("paymentMethod") String paymentMethod,
			@RequestParam("address") String address,
			Model model,
			Principal principal) {

		Account account = currentUser(principal);
		if (account == null) {
			return "redirect:/login";
		}

		List<CartView> cartList = cartService.list(account.getId());
		if (cartList == null || cartList.isEmpty()) {
			return "redirect:/voidrshop/cart";
		}

		int totalPrice = cartList.stream()
				.mapToInt(cv -> cv.getItem().getPrice() * cv.getCart().getQuantity())
				.sum();

		model.addAttribute("cartItems", cartList);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("paymentMethod", paymentMethod);
		model.addAttribute("address", address);
		return "shop/purchase/purchase_confirm";
	}

	/** 購入完了処理 */
	@PostMapping("/complete")
	@Transactional
	public String completePurchase(
			@RequestParam("paymentMethod") String paymentMethod,
			@RequestParam("address") String address,
			Principal principal,
			Model model) {

		Account account = currentUser(principal);
		if (account == null) {
			return "redirect:/login";
		}

		List<CartView> cartList = cartService.list(account.getId());
		if (cartList == null || cartList.isEmpty()) {
			return "redirect:/voidrshop/cart";
		}

		int totalPrice = cartList.stream()
				.mapToInt(cv -> cv.getItem().getPrice() * cv.getCart().getQuantity())
				.sum();

		// =====================
		// 1. 注文リスト登録
		// =====================
		OrderList orderList = new OrderList();
		orderList.setUsername(account.getUsername()); // ← usernameを設定（OrderListServiceImpl仕様）
		orderListService.createOrderList(orderList); // DB登録 + userId自動セット

		// =====================
		// 2. 個々の注文登録
		// =====================
		for (CartView cv : cartList) {
			Order order = new Order();
			order.setOrderListId(orderList.getId());
			order.setItemId(cv.getItem().getId());
			orderService.createOrder(order);

			// =====================
			// 3. 注文アイテム登録
			// =====================
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderId(order.getId());
			orderItem.setItemId(cv.getItem().getId());
			orderItem.setQuantity(cv.getCart().getQuantity());
			orderItem.setPrice(cv.getItem().getPrice());
			orderItemService.addOrderItem(orderItem);
		}

		// =====================
		// 4. カートを空にする
		// =====================
		cartService.clearCart(account.getUsername());

		// =====================
		// 5. ビュー用データ
		// =====================
		model.addAttribute("orderListId", orderList.getId());
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("paymentMethod", paymentMethod);
		model.addAttribute("address", address);

		return "shop/purchase/purchase_complete";
	}
}
