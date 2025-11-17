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

	/** 最短配達日を取得（注文から2営業日後） */
	private LocalDate getEarliestDeliveryDate() {
		LocalDate today = LocalDate.now();
		return getNextBusinessDay(today.plusDays(2));
	}

	/** 配達希望日候補（最短日〜2週間後まで、営業日のみ） */
	private List<LocalDate> getDeliveryOptions() {
		List<LocalDate> options = new ArrayList<>();
		LocalDate date = getEarliestDeliveryDate();
		LocalDate endDate = date.plusDays(13); // 最短日含めて2週間分
		while (!date.isAfter(endDate)) {
			options.add(date);
			date = getNextBusinessDay(date.plusDays(1));
		}
		return options;
	}

	// 追加：購入対象となる「販売中の商品」だけを抽出
	private List<CartView> filterPurchasable(List<CartView> cartList) {
		if (cartList == null) {
			return Collections.emptyList();
		}
		return cartList.stream()
				.filter(cv -> cv != null
						&& cv.getItem() != null
						&& !cv.isItemDeleted())
				.toList();
	}

	// 追加：カート内に販売終了商品が含まれているか
	private boolean hasDeletedItems(List<CartView> cartList) {
		if (cartList == null) {
			return false;
		}
		return cartList.stream()
				.anyMatch(cv -> cv != null && cv.isItemDeleted());
	}

	/** 購入画面表示 */
	@GetMapping
	public String showPurchasePage(Model model, Principal principal) {
		Account account = currentUser(principal);
		if (account == null) {
			return "redirect:/login";
		}

		List<CartView> allCart = cartService.list(account.getId());
		List<CartView> purchasable = filterPurchasable(allCart);
		boolean hasDeleted = hasDeletedItems(allCart);

		// 購入可能な商品が 0 件の場合
		if (purchasable.isEmpty()) {
			model.addAttribute("cartItems", Collections.emptyList());
			model.addAttribute("totalPrice", 0);
			model.addAttribute("shippingFee", 0);
			model.addAttribute("finalTotal", 0);
			if (hasDeleted) {
				model.addAttribute("message", "カート内の商品は現在すべて販売終了のため、ご購入いただけません。カート画面でご確認ください。");
			} else {
				model.addAttribute("message", "カートに商品がありません。");
			}
			model.addAttribute("deliveryOptions", getDeliveryOptions());
			model.addAttribute("hasDeletedItems", hasDeleted);
			return "shop/purchase/purchase";
		}

		int total = purchasable.stream()
				.mapToInt(cv -> cv.getItem().getPrice() * cv.getCart().getQuantity())
				.sum();
		int shippingFee = calcShippingFee(total);
		int finalTotal = total + shippingFee;

		model.addAttribute("cartItems", purchasable);
		model.addAttribute("totalPrice", total);
		model.addAttribute("shippingFee", shippingFee);
		model.addAttribute("finalTotal", finalTotal);

		// 配達希望日候補
		model.addAttribute("deliveryOptions", getDeliveryOptions());

		// 販売終了商品が含まれていた場合の注意（購入対象には含めない）
		model.addAttribute("hasDeletedItems", hasDeleted);

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

		List<CartView> allCart = cartService.list(account.getId());
		List<CartView> purchasable = filterPurchasable(allCart);
		boolean hasDeleted = hasDeletedItems(allCart);

		if (purchasable.isEmpty()) {
			// 販売終了しかない場合はカートへ戻す
			return "redirect:/voidrshop/cart";
		}

		int totalPrice = purchasable.stream()
				.mapToInt(cv -> cv.getItem().getPrice() * cv.getCart().getQuantity())
				.sum();
		int shippingFee = calcShippingFee(totalPrice);
		int finalTotal = totalPrice + shippingFee;

		model.addAttribute("cartItems", purchasable);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("shippingFee", shippingFee);
		model.addAttribute("finalTotal", finalTotal);

		model.addAttribute("paymentMethod", paymentMethod);
		model.addAttribute("address", address);
		model.addAttribute("deliveryDate", deliveryDate);
		model.addAttribute("deliveryTime", deliveryTime);

		// 販売終了品がカートにあった場合の注意
		model.addAttribute("hasDeletedItems", hasDeleted);

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

		// カート取得（販売中のみ購入対象）
		List<CartView> allCart = cartService.list(account.getId());
		List<CartView> purchasable = filterPurchasable(allCart);

		if (purchasable.isEmpty()) {
			// 有効な商品がなければカートへ
			return "redirect:/voidrshop/cart";
		}

		int totalPrice = purchasable.stream()
				.mapToInt(cv -> cv.getItem().getPrice() * cv.getCart().getQuantity())
				.sum();
		int shippingFee = calcShippingFee(totalPrice);
		int finalTotal = totalPrice + shippingFee;

		// ★配達希望日チェック（最短日以降に補正）→ DB保存にもこの値を使う
		LocalDate deliveryDateValue = LocalDate.parse(deliveryDate);
		LocalDate earliest = getEarliestDeliveryDate();
		if (deliveryDateValue.isBefore(earliest)) {
			deliveryDateValue = earliest;
		}

		// ★注文リスト登録（購入情報も一緒に保存）
		OrderList orderList = new OrderList();
		orderList.setUsername(account.getUsername());
		orderList.setPaymentMethod(paymentMethod);
		orderList.setAddress(address);
		orderList.setDeliveryDate(deliveryDateValue);
		orderList.setDeliveryTime(deliveryTime);
		
		// 確定した送料・合計金額を保存
		orderList.setShippingFee(shippingFee);
		orderList.setFinalTotal(finalTotal);
		
		// status は OrderListServiceImpl.createOrderList() 側で NEW に初期化
		orderListService.createOrderList(orderList);

		// 個別注文登録（販売中の商品だけ）
		for (CartView cv : purchasable) {
			Order order = new Order();
			order.setOrderListId(orderList.getId());
			order.setItemId(cv.getItem().getId());
			orderService.createOrder(order);

			// 注文アイテム登録
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderId(order.getId());
			orderItem.setItemId(cv.getItem().getId());
			orderItem.setQuantity(cv.getCart().getQuantity());
			orderItem.setPrice(cv.getItem().getPrice());
			orderItemService.addOrderItem(orderItem);
		}

		// カートを空にする（販売終了品も含めてクリア）
		cartService.clearCart(account.getUsername());

		// 画面表示用
		model.addAttribute("orderListId", orderList.getId());
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("shippingFee", shippingFee);
		model.addAttribute("finalTotal", finalTotal);
		model.addAttribute("paymentMethod", paymentMethod);
		model.addAttribute("address", address);
		model.addAttribute("deliveryDate", deliveryDateValue);
		model.addAttribute("deliveryTime", deliveryTime);
		model.addAttribute("emailNotice", "ご登録のメールアドレスに注文詳細をお送りしました。");

		return "shop/purchase/purchase_complete";
	}

}
