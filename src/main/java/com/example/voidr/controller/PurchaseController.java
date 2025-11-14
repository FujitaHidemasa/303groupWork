package com.example.voidr.controller;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
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

	/** 送料を計算（5000円以上 → 無料） */
	private int calcShippingFee(int totalPrice) {
		return (totalPrice >= 5000) ? 0 : 500;
	}

	/** 土日を考慮して翌営業日にする */
	private LocalDate getNextBusinessDay(LocalDate date) {
		while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
			date = date.plusDays(1);
		}
		return date;
	}

	/** 配達希望日の候補（注文から2〜3営業日以内） */
	private List<LocalDate> getDeliveryOptions() {
		LocalDate today = LocalDate.now();
		List<LocalDate> options = new ArrayList<>();
		options.add(getNextBusinessDay(today.plusDays(2)));
		options.add(getNextBusinessDay(today.plusDays(3)));
		return options;
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
			model.addAttribute("shippingFee", 0);
			model.addAttribute("finalTotal", 0);
			model.addAttribute("message", "カートに商品がありません。");
			return "shop/purchase/purchase";
		}

		int total = cartList.stream()
				.mapToInt(cv -> cv.getItem().getPrice() * cv.getCart().getQuantity())
				.sum();

		int shippingFee = calcShippingFee(total);
		int finalTotal = total + shippingFee;

		model.addAttribute("cartItems", cartList);
		model.addAttribute("totalPrice", total);
		model.addAttribute("shippingFee", shippingFee);
		model.addAttribute("finalTotal", finalTotal);

		// 配達希望日候補
		model.addAttribute("deliveryOptions", getDeliveryOptions());

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
			@RequestParam("deliveryDate") String deliveryDate,
			@RequestParam("deliveryTime") String deliveryTime,
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

		int shippingFee = calcShippingFee(totalPrice);
		int finalTotal = totalPrice + shippingFee;

		model.addAttribute("cartItems", cartList);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("shippingFee", shippingFee);
		model.addAttribute("finalTotal", finalTotal);

		model.addAttribute("paymentMethod", paymentMethod);
		model.addAttribute("address", address);
		model.addAttribute("deliveryDate", deliveryDate);
		model.addAttribute("deliveryTime", deliveryTime);

		return "shop/purchase/purchase_confirm";
	}

	/** 購入完了処理 */
	@PostMapping("/complete")
	@Transactional
	public String completePurchase(
			@RequestParam("paymentMethod") String paymentMethod,
			@RequestParam("address") String address,
			@RequestParam("deliveryDate") String deliveryDate,
			@RequestParam("deliveryTime") String deliveryTime,
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

		int shippingFee = calcShippingFee(totalPrice);
		int finalTotal = totalPrice + shippingFee;

		// =====================
		// 1. 注文リスト登録
		// =====================
		OrderList orderList = new OrderList();
		orderList.setUsername(account.getUsername());
		orderListService.createOrderList(orderList);

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
		model.addAttribute("shippingFee", shippingFee);
		model.addAttribute("finalTotal", finalTotal);

		model.addAttribute("paymentMethod", paymentMethod);
		model.addAttribute("address", address);

		// 配達希望日（注文から2〜3営業日以内の範囲チェック）
		LocalDate deliveryDateValue = LocalDate.parse(deliveryDate);
		LocalDate earliest = getDeliveryOptions().get(0);
		LocalDate latest = getDeliveryOptions().get(1);

		if (deliveryDateValue.isBefore(earliest))
			deliveryDateValue = earliest;
		if (deliveryDateValue.isAfter(latest))
			deliveryDateValue = latest;

		model.addAttribute("deliveryDate", deliveryDateValue);
		model.addAttribute("deliveryTime", deliveryTime);

		// メール送信などもここで可能（別途サービスを作成して送る）
		model.addAttribute("emailNotice", "ご登録のメールアドレスに注文詳細をお送りしました。");

		return "shop/purchase/purchase_complete";
	}

}
