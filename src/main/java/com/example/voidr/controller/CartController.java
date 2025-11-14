package com.example.voidr.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.voidr.entity.Account;
import com.example.voidr.service.AccountService;
import com.example.voidr.service.CartService;
import com.example.voidr.view.CartView;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/voidrshop/cart")
@RequiredArgsConstructor
@Validated
@PreAuthorize("isAuthenticated()")
public class CartController {

	private final CartService cartService;
	private final AccountService accountService;

	/** ログイン中ユーザーのID取得 */
	private long currentUserId(Principal principal) {
		Account acc = accountService.findByUsername(principal.getName());
		return acc.getId();
	}

	/** カート一覧表示 */
	@GetMapping
	public String list(Model model, Principal principal) {
		long userId = currentUserId(principal);
		List<CartView> items = cartService.list(userId);
		int total = cartService.sumTotal(userId);
		model.addAttribute("cartItems", items);
		model.addAttribute("total", total);
		return "shop/cart/list";
	}

	/** 商品追加（Ajax対応） */
	@PostMapping("/add")
	public Object add(@RequestParam("itemId") long itemId,
			@RequestParam(name = "quantity", defaultValue = "1") int quantity,
			Principal principal,
			HttpServletRequest request) {
		long userId = currentUserId(principal);
		cartService.addItem(userId, itemId, Math.max(1, quantity));

		// Ajaxの場合はJSONを返す
		if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
			int newCount = cartService.countInBadge(userId); // ✅ 追加後のバッジ数を返す
			return ResponseEntity.ok(Map.of(
					"status", "ok",
					"message", "商品をカートに追加しました",
					"count", newCount));
		}

		// 通常のフォーム送信時はカートページへリダイレクト
		return "redirect:/voidrshop/cart";
	}

	/** 数量変更（DBに即反映） */
	@PostMapping("/change")
	public String change(@RequestParam("cartId") long cartId,
			@RequestParam("quantity") int quantity,
			Principal principal) {
		long userId = currentUserId(principal);
		cartService.changeQuantity(userId, cartId, Math.max(1, quantity));
		return "redirect:/voidrshop/cart";
	}

	/** 削除 */
	@PostMapping("/remove")
	public String remove(@RequestParam("cartId") long cartId, Principal principal) {
		long userId = currentUserId(principal);
		cartService.remove(userId, cartId);
		return "redirect:/voidrshop/cart";
	}

	/** ✅ バッジ用：カート内商品数を返す（JSON） */
	@PreAuthorize("permitAll()") /** 11/13追加（谷口）：バッジ用APIは未ログインでもOKにする */
	@GetMapping("/count")
	@ResponseBody
	public Map<String, Integer> count(Principal principal) {
		if (principal == null) {
			return Map.of("count", 0);
		}
		long userId = currentUserId(principal);
		return Map.of("count", cartService.countInBadge(userId));
	}
}
