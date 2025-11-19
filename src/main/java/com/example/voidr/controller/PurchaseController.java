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

	/* ============================
	 * 祝日 & 営業日 判定
	 * ============================ */

	private boolean isHoliday(LocalDate date) {
		int year = date.getYear();

		List<LocalDate> holidays = List.of(
				LocalDate.of(year, 1, 1),
				LocalDate.of(year, 2, 11),
				LocalDate.of(year, 4, 29),
				LocalDate.of(year, 5, 3),
				LocalDate.of(year, 5, 4),
				LocalDate.of(year, 5, 5),
				LocalDate.of(year, 11, 3),
				LocalDate.of(year, 11, 23)
		);

		return holidays.contains(date);
	}

	private boolean isBusinessDay(LocalDate date) {
		DayOfWeek w = date.getDayOfWeek();
		if (w == DayOfWeek.SATURDAY || w == DayOfWeek.SUNDAY) {
			return false;
		}
		return !isHoliday(date);
	}

	private LocalDate getNextBusinessDay(LocalDate date) {
		while (!isBusinessDay(date)) {
			date = date.plusDays(1);
		}
		return date;
	}

	/* ============================
	 * 配達日ロジック
	 * ============================ */

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

	private LocalDate getEarliestDeliveryDate() {
		LocalDate delivery = getShipmentDate().plusDays(1);
		return getNextBusinessDay(delivery);
	}

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

	/* ============================
	 * カートフィルタ
	 * ============================ */

	private List<CartView> filterPurchasable(List<CartView> cartList) {
		if (cartList == null) return Collections.emptyList();

		return cartList.stream()
				.filter(cv -> cv != null && cv.getItem() != null && !cv.isItemDeleted())
				.toList();
	}

	private boolean hasDeletedItems(List<CartView> cartList) {
		if (cartList == null) return false;

		return cartList.stream()
				.anyMatch(cv -> cv != null && cv.isItemDeleted());
	}

	/* ============================
	 * 画面遷移
	 * ============================ */

	/** 購入画面表示 */
	@GetMapping
	public String showPurchasePage(Model model, Principal principal) {
		Account account = currentUser(principal);
		if (account == null) return "redirect:/login";

		List<CartView> allCart = cartService.list(account.getId());
		List<CartView> purchasable = filterPurchasable(allCart);
		boolean hasDeleted = hasDeletedItems(allCart);

		// 配送先候補（お届け先管理）
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
			model.addAttribute("addresses", addresses);
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

	@PostMapping("/confirm")
	public String confirmPurchase(
	        @RequestParam("paymentMethod") String paymentMethod,
	        @RequestParam(value = "addressSelect", required = false) String addressSelect,
	        @RequestParam(value = "manualAddress", required = false) String manualAddress,
	        @RequestParam("deliveryDate") String deliveryDate,
	        @RequestParam("deliveryTime") String deliveryTime,
	        Model model, Principal principal) {

	    Account account = currentUser(principal);
	    if (account == null) return "redirect:/login";

	    List<CartView> allCart = cartService.list(account.getId());
	    List<CartView> purchasable = filterPurchasable(allCart);
	    if (purchasable.isEmpty()) return "redirect:/voidrshop/cart";

	    int total = purchasable.stream()
	            .mapToInt(cv -> cv.getItem().getPrice() * cv.getCart().getQuantity())
	            .sum();
	    int shippingFee = calcShippingFee(total);
	    int finalTotal = total + shippingFee;

	    /* ▼▼ 配送先情報の決定 ▼▼ */
	    String finalRecipientName = account.getDisplayName();
	    String finalPostalCode = "";
	    String finalAddress = "";
	    String finalPhone = account.getPhoneNumber() != null ? account.getPhoneNumber() : "";

	    // addressSelect → 住所帳のID or "manual"
	    Long addressId = null;
	    if (addressSelect != null && !addressSelect.isBlank() && !"manual".equals(addressSelect)) {
	        try {
	            addressId = Long.valueOf(addressSelect);
	        } catch (NumberFormatException ex) {
	            addressId = null;
	        }
	    }

	    if (addressId != null) {
	        // 住所帳側から検索
	        List<Address> addresses = addressService.getAddressesByUserId(account.getId());
	        Address found = null;
	        for (Address a : addresses) {
	            if (a.getId().equals(addressId)) {
	                found = a;
	                break;
	            }
	        }
	        if (found != null) {
	            // 氏名：住所帳にあればそれを優先、なければ会員情報の氏名
	            if (found.getRecipientName() != null && !found.getRecipientName().isBlank()) {
	                finalRecipientName = found.getRecipientName();
	            }
	            finalPostalCode = found.getPostalCode();
	            finalAddress = found.getAddress();
	            finalPhone = found.getPhone();
	        }

	    } else if (manualAddress != null && !manualAddress.isBlank()) {
	        // 手入力住所（氏名は会員情報の氏名）
	        finalAddress = manualAddress;
	    }
	    /* ▲▲ ここが最重要 ▲▲ */

	    // お届け先が決まっていなければ購入画面に戻す
	    if (finalAddress == null || finalAddress.isBlank()) {
	        // 購入画面に必要な情報を再セット
	        model.addAttribute("cartItems", purchasable);
	        model.addAttribute("totalPrice", total);
	        model.addAttribute("shippingFee", shippingFee);
	        model.addAttribute("finalTotal", finalTotal);

	        model.addAttribute("deliveryOptions", getDeliveryOptions());
	        model.addAttribute("hasDeletedItems", hasDeletedItems(allCart));
	        model.addAttribute("addresses", addressService.getAddressesByUserId(account.getId()));

	        // 入力済み値も戻しておく
	        model.addAttribute("paymentMethod", paymentMethod);
	        model.addAttribute("deliveryDate", deliveryDate);
	        model.addAttribute("deliveryTime", deliveryTime);

	        // エラーメッセージ
	        model.addAttribute("addressError", "お届け先を選択するか、住所を入力してください。");

	        return "shop/purchase/purchase";
	    }

	    // ▼ 確認画面へ
	    model.addAttribute("cartItems", purchasable);
	    model.addAttribute("totalPrice", total);
	    model.addAttribute("shippingFee", shippingFee);
	    model.addAttribute("finalTotal", finalTotal);

	    model.addAttribute("paymentMethod", paymentMethod);
	    model.addAttribute("recipientName", finalRecipientName);
	    model.addAttribute("postalCode", finalPostalCode);
	    model.addAttribute("address", finalAddress);
	    model.addAttribute("phoneNumber", finalPhone);
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
			@RequestParam("recipientName") String recipientName,
			@RequestParam("postalCode") String postalCode,
			@RequestParam("address") String address,
			@RequestParam("phoneNumber") String phoneNumber,
			@RequestParam("deliveryDate") String deliveryDate,
			@RequestParam("deliveryTime") String deliveryTime,
			Model model, Principal principal) {

		Account account = currentUser(principal);
		if (account == null) return "redirect:/login";

		List<CartView> allCart = cartService.list(account.getId());
		List<CartView> purchasable = filterPurchasable(allCart);
		if (purchasable.isEmpty()) return "redirect:/voidrshop/cart";

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

		// 注文リスト保存（DB は従来通り address のみ）
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

		// 最新の注文情報を取得（created_at 用）
		OrderList freshOrderList = orderListService.findById(orderList.getId());

		// 購入完了メール送信（氏名は recipientName を優先）
		try {
			PurchaseReceiptDto dto = new PurchaseReceiptDto();
			dto.setDisplayName(account.getDisplayName()); // フォールバック用
			dto.setRecipientName(recipientName);          // お届け先氏名（住所帳 or 会員情報）
			dto.setAddress(address);
			dto.setPhoneNumber(phoneNumber);
			dto.setOrderId(orderList.getId());
			dto.setOrderDateTime(freshOrderList != null ? freshOrderList.getCreatedAt() : null);
			dto.setPaymentMethod(paymentMethod);
			dto.setSubtotal(total);
			dto.setShippingFee(shippingFee);
			dto.setFinalTotal(finalTotal);

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
			model.addAttribute("emailNotice", "ご登録のメールアドレスに注文内容を送信しました。");

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("emailNotice", "ご注文は完了しましたが、メール送信に失敗しました。");
		}

		// 完了画面表示
		model.addAttribute("orderListId", orderList.getId());
		model.addAttribute("totalPrice", total);
		model.addAttribute("shippingFee", shippingFee);
		model.addAttribute("finalTotal", finalTotal);
		model.addAttribute("paymentMethod", paymentMethod);
		model.addAttribute("recipientName", recipientName);
		model.addAttribute("postalCode", postalCode);
		model.addAttribute("address", address);
		model.addAttribute("phoneNumber", phoneNumber);
		model.addAttribute("deliveryDate", deliveryValue);
		model.addAttribute("deliveryTime", deliveryTime);

		return "shop/purchase/purchase_complete";
	}
}
