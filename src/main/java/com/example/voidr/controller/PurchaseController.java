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
import com.example.voidr.entity.Address;
import com.example.voidr.entity.Order;
import com.example.voidr.entity.OrderItem;
import com.example.voidr.entity.OrderList;
import com.example.voidr.service.AccountService;
import com.example.voidr.service.AddressService;
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
	private final AddressService addressService;


	/** ログイン中ユーザー情報取得 */
	private Account currentUser(Principal principal) {
		return accountService.findByUsername(principal.getName());
	}

	/** 送料を計算（5000円以上 → 無料） */
	private int calcShippingFee(int totalPrice) {
		return (totalPrice >= 5000) ? 0 : 500;
	}

	/* ----------------------------
	 * 祝日 & 営業日 判定追加
	 * ---------------------------- */

	/** 祝日判定（必要なら祝日を追加） */
	private boolean isHoliday(LocalDate date) {
		int year = date.getYear();

		// 固定祝日
		List<LocalDate> holidays = List.of(
				LocalDate.of(year, 1, 1), // 元日
				LocalDate.of(year, 2, 11), // 建国記念の日
				LocalDate.of(year, 4, 29), // 昭和の日
				LocalDate.of(year, 5, 3), // 憲法記念日
				LocalDate.of(year, 5, 4), // みどりの日
				LocalDate.of(year, 5, 5), // こどもの日
				LocalDate.of(year, 11, 3), // 文化の日
				LocalDate.of(year, 11, 23) // 勤労感謝の日
		);

		if (holidays.contains(date)) {
			return true;
		}

		// 春分・秋分など可変祝日は必要なら計算可能（今は省略）
		return false;
	}

	/** 営業日判定（土日 + 祝日 を除外） */
	private boolean isBusinessDay(LocalDate date) {
		DayOfWeek w = date.getDayOfWeek();
		if (w == DayOfWeek.SATURDAY || w == DayOfWeek.SUNDAY) {
			return false;
		}
		if (isHoliday(date)) {
			return false;
		}
		return true;
	}

	/** 営業日のみ進める（次の営業日を返す） */
	private LocalDate getNextBusinessDay(LocalDate date) {
		while (!isBusinessDay(date)) {
			date = date.plusDays(1);
		}
		return date;
	}

	/* ----------------------------
	 * 配達日（A の仕様：発送 2〜3営業日 → 配達）
	 * 今回は 2営業日後発送 → 翌営業日着
	 * ---------------------------- */

	/** 注文日から 2 営業日後（発送日） */
	private LocalDate getShipmentDate() {
		LocalDate date = LocalDate.now();
		int need = 2;
		int count = 0;

		while (count < need) {
			date = date.plusDays(1);
			if (isBusinessDay(date)) {
				count++;
			}
		}
		return date;
	}

	/** 発送日の翌営業日 → 最短配達日 */
	private LocalDate getEarliestDeliveryDate() {
		LocalDate delivery = getShipmentDate().plusDays(1);
		return getNextBusinessDay(delivery);
	}

	/** 配達希望日候補（最短〜2週間後） */
	private List<LocalDate> getDeliveryOptions() {
		List<LocalDate> list = new ArrayList<>();

		LocalDate date = getEarliestDeliveryDate();
		LocalDate end = date.plusDays(13);

		while (!date.isAfter(end)) {
			if (isBusinessDay(date)) {
				list.add(date);
			}
			date = date.plusDays(1);
		}
		return list;
	}

	/* ----------------------------
	 * カートフィルタ（既存のまま）
	 * ---------------------------- */

	private List<CartView> filterPurchasable(List<CartView> cartList) {
		if (cartList == null)
			return Collections.emptyList();

		return cartList.stream()
				.filter(cv -> cv != null && cv.getItem() != null && !cv.isItemDeleted())
				.toList();
	}

	private boolean hasDeletedItems(List<CartView> cartList) {
		if (cartList == null)
			return false;

		return cartList.stream()
				.anyMatch(cv -> cv != null && cv.isItemDeleted());
	}

	/* ----------------------------
	 * 以下、既存の show / confirm / complete を
	 * A の配達日仕様のまま利用
	 * ---------------------------- */

	/** 購入画面表示 */
	@GetMapping
	public String showPurchasePage(Model model, Principal principal) {
		Account account = currentUser(principal);
		if (account == null)
			return "redirect:/login";

		List<CartView> allCart = cartService.list(account.getId());
		List<CartView> purchasable = filterPurchasable(allCart);
		boolean hasDeleted = hasDeletedItems(allCart);
		
		// ▼ ★ 追加：配送先住所を取得
	    List<Address> addresses = addressService.getAddressesByUserId(account.getId());
	    
	    
	    

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
		int shipping = calcShippingFee(total);
		int finalTotal = total + shipping;

		model.addAttribute("cartItems", purchasable);
		model.addAttribute("totalPrice", total);
		model.addAttribute("shippingFee", shipping);
		model.addAttribute("finalTotal", finalTotal);

		model.addAttribute("deliveryOptions", getDeliveryOptions());
		model.addAttribute("hasDeletedItems", hasDeleted);
		
		model.addAttribute("addresses", addresses);

		return "shop/purchase/purchase";
	}

	/** 商品詳細ページから直接購入 */
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
	        @RequestParam(value = "addressSelect", required = false) Long addressId,
	        @RequestParam(value = "manualAddress", required = false) String manualAddress,
	        @RequestParam("deliveryDate") String deliveryDate,
	        @RequestParam("deliveryTime") String deliveryTime,
	        Model model, Principal principal) {

	    Account account = currentUser(principal);
	    if (account == null)
	        return "redirect:/login";

	    List<CartView> allCart = cartService.list(account.getId());
	    List<CartView> purchasable = filterPurchasable(allCart);

	    if (purchasable.isEmpty())
	        return "redirect:/voidrshop/cart";

	    int total = purchasable.stream()
	            .mapToInt(cv -> cv.getItem().getPrice() * cv.getCart().getQuantity())
	            .sum();
	    int shippingFee = calcShippingFee(total);
	    int finalTotal = total + shippingFee;

	    /* ▼▼ ここが最重要 ▼▼ */
	    String finalAddress = "";

	    if (addressId != null) {
	        // 🔹 登録済み住所のIDが送られてきた場合は、DBから住所を取り出す
	        Address addr = addressService.getAddressesByUserId(account.getId())
	                .stream()
	                .filter(a -> a.getId().equals(addressId))
	                .findFirst()
	                .orElse(null);

	        if (addr != null) {
	            finalAddress = addr.getAddress();  // ← 表示は「住所だけ」
	        }

	    } else if (manualAddress != null && !manualAddress.isBlank()) {
	        // 🔹 手動入力の場合はこちら
	        finalAddress = manualAddress;
	    }

	    /* ▲▲ ここまで最重要 ▲▲ */

	    model.addAttribute("cartItems", purchasable);
	    model.addAttribute("totalPrice", total);
	    model.addAttribute("shippingFee", shippingFee);
	    model.addAttribute("finalTotal", finalTotal);

	    model.addAttribute("paymentMethod", paymentMethod);
	    model.addAttribute("address", finalAddress);  // ← ここに住所だけが入る！
	    model.addAttribute("deliveryDate", deliveryDate);
	    model.addAttribute("deliveryTime", deliveryTime);
	    model.addAttribute("hasDeletedItems", hasDeletedItems(allCart));

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
			Model model, Principal principal) {

		Account account = currentUser(principal);
		if (account == null)
			return "redirect:/login";

		List<CartView> allCart = cartService.list(account.getId());
		List<CartView> purchasable = filterPurchasable(allCart);
		if (purchasable.isEmpty())
			return "redirect:/voidrshop/cart";

		int total = purchasable.stream()
				.mapToInt(cv -> cv.getItem().getPrice() * cv.getCart().getQuantity())
				.sum();
		int shippingFee = calcShippingFee(total);
		int finalTotal = total + shippingFee;

		// 最短配達日に補正
		LocalDate deliveryValue = LocalDate.parse(deliveryDate);
		LocalDate earliest = getEarliestDeliveryDate();
		if (deliveryValue.isBefore(earliest)) {
			deliveryValue = earliest;
		}

		// 注文リスト保存
		OrderList orderList = new OrderList();
		orderList.setUsername(account.getUsername());
		orderList.setPaymentMethod(paymentMethod);
		orderList.setAddress(address);
		orderList.setDeliveryDate(deliveryValue);
		orderList.setDeliveryTime(deliveryTime);
		orderList.setShippingFee(shippingFee);
		orderList.setFinalTotal(finalTotal);
		orderListService.createOrderList(orderList);

		// 商品ごとに注文保存
		for (CartView cv : purchasable) {
			Order order = new Order();
			order.setOrderListId(orderList.getId());
			order.setItemId(cv.getItem().getId());
			orderService.createOrder(order);

			OrderItem oi = new OrderItem();
			oi.setOrderId(order.getId());
			oi.setItemId(cv.getItem().getId());
			oi.setQuantity(cv.getCart().getQuantity());
			oi.setPrice(cv.getItem().getPrice());
			orderItemService.addOrderItem(oi);
		}

		// カート全削除
		cartService.clearCart(account.getUsername());

		// 完了画面表示
		model.addAttribute("orderListId", orderList.getId());
		model.addAttribute("totalPrice", total);
		model.addAttribute("shippingFee", shippingFee);
		model.addAttribute("finalTotal", finalTotal);
		model.addAttribute("paymentMethod", paymentMethod);
		model.addAttribute("address", address);
		model.addAttribute("deliveryDate", deliveryValue);
		model.addAttribute("deliveryTime", deliveryTime);
		model.addAttribute("emailNotice", "ご登録のメールアドレスに注文詳細をお送りしました。");

		return "shop/purchase/purchase_complete";
	}

}
