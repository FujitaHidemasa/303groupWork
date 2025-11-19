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

import com.example.voidr.dto.PurchaseReceiptDto;
import com.example.voidr.entity.Account;
import com.example.voidr.entity.Address;
import com.example.voidr.entity.Order;
import com.example.voidr.entity.OrderItem;
import com.example.voidr.entity.OrderList;
import com.example.voidr.service.AccountService;
import com.example.voidr.service.AddressService;
import com.example.voidr.service.CartService;
import com.example.voidr.service.EmailService;
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
	private final EmailService emailService;


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
	        @RequestParam(value = "addressSelect", required = false) String addressSelect,
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
	    
	    // ★追加: addressSelect を Long に変換（"manual" の場合は null のまま）
	    Long addressId = null;
	    if (addressSelect != null && !addressSelect.isBlank() && !"manual".equals(addressSelect)) {
	        try {
	            addressId = Long.valueOf(addressSelect);
	        } catch (NumberFormatException ex) {
	            // 想定外の値が来たときは登録住所は使わず、手入力側にフォールバック
	            addressId = null;
	        }
	    }

	    /* ▼▼ ここが最重要 ▼▼ */
	    String finalAddress = "";

	    if (addressId != null) {
	        // 登録済み住所IDが来ている場合は、for文で該当レコードを探す（ラムダを使わない）
	        List<Address> addresses = addressService.getAddressesByUserId(account.getId());
	        Address found = null;
	        for (Address a : addresses) {
	            if (a.getId().equals(addressId)) {
	                found = a;
	                break;
	            }
	        }
	        if (found != null) {
	            finalAddress = found.getAddress();
	        }

	    } else if (manualAddress != null && !manualAddress.isBlank()) {
	        // 「直接入力する」＋手入力住所の場合はこちら
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
		
		// ★追加: 注文情報を再取得（created_at を含めてDTOに詰める）
		OrderList freshOrderList = orderListService.findById(orderList.getId());

		// ★追加: 購入完了メール送信（失敗しても注文は残す）
		try {

			PurchaseReceiptDto dto = new PurchaseReceiptDto();
			dto.setDisplayName(account.getDisplayName());
			dto.setAddress(address);
			dto.setPhoneNumber(account.getPhoneNumber());
			dto.setOrderId(orderList.getId());
			dto.setOrderDateTime(freshOrderList != null ? freshOrderList.getCreatedAt() : null);
			dto.setPaymentMethod(paymentMethod);
			dto.setSubtotal(total);
			dto.setShippingFee(shippingFee);
			dto.setFinalTotal(finalTotal);

			// 商品明細
			List<PurchaseReceiptDto.Item> dtoItems = new ArrayList<>();
			for (CartView cv : purchasable) {
				int unitPrice = cv.getItem().getPrice();
				int quantity = cv.getCart().getQuantity();
				dtoItems.add(new PurchaseReceiptDto.Item(
						cv.getItem().getName(),
						unitPrice,
						quantity,
						unitPrice * quantity));
			}
			dto.setItems(dtoItems);

			emailService.sendPurchaseReceipt(account.getEmail(), dto);
			// ★成功メッセージ
			model.addAttribute("emailNotice", "ご登録のメールアドレスに注文内容を送信しました。");

		} catch (Exception e) {
			// ★失敗してもロールバックしない（注文は確定済み）
			e.printStackTrace(); // TODO: logger に変更推奨
			model.addAttribute("emailNotice", "ご注文は完了しましたが、メール送信に失敗しました。");
		}

		// 完了画面表示
		model.addAttribute("orderListId", orderList.getId());
		model.addAttribute("totalPrice", total);
		model.addAttribute("shippingFee", shippingFee);
		model.addAttribute("finalTotal", finalTotal);
		model.addAttribute("paymentMethod", paymentMethod);
		model.addAttribute("address", address);
		model.addAttribute("deliveryDate", deliveryValue);
		model.addAttribute("deliveryTime", deliveryTime);

		return "shop/purchase/purchase_complete";
	}

}
