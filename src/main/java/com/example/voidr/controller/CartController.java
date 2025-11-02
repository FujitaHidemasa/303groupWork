package com.example.voidr.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
//★追加（import）
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
// ★PostMapping等まとめて
// import org.springframework.web.servlet.mvc.support.RedirectAttributes; // ★必要なら復活
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.voidr.entity.Cart; // ★追加：add用 Cart の生成に使用
import com.example.voidr.entity.LoginUser;
import com.example.voidr.service.CartService;
import com.example.voidr.service.ItemService;
import com.example.voidr.view.CartView;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/voidrshop/cart")
@RequiredArgsConstructor
public class CartController 
{
	private final ItemService itemService;
	private final CartService cartService;

	/** カート一覧ページ */
	@GetMapping
	public String cartList(Model model, @AuthenticationPrincipal LoginUser loginUser) 
	{
		itemService.syncItems();

		// カートと商品情報をまとめて取得
		List<CartView> cartViews = cartService.getCartViewByUserId(loginUser.getId());

		// is_hold = true / false で分割
		var holdItems = cartViews.stream().filter(v -> v.getCart().isHold()).toList();
		var normalItems = cartViews.stream().filter(v -> !v.getCart().isHold()).toList();

		model.addAttribute("holdItems", holdItems);
		model.addAttribute("normalItems", normalItems);

		// ★追加：テンプレで「カートを空にする」用に cartListId を渡す
		long cartListId = !cartViews.isEmpty()
				? cartViews.get(0).getCart().getCartListId()
				: cartService.ensureCartListId(loginUser.getId(), true); // ★追加
		model.addAttribute("cartListId", cartListId); // ★追加

		return "shop/cart/list";
	}

	// ================================
	// ★追加：カートに追加（一覧/詳細からPOST）
	// ================================
	@PostMapping("/add")
	public String addToCart(@RequestParam long itemId,
			@RequestParam(defaultValue = "1") int quantity,
			@AuthenticationPrincipal LoginUser loginUser) 
	{
		long cartListId = cartService.ensureCartListId(loginUser.getId(), true); // ★追加

		Cart cart = new Cart(); // ★追加
		cart.setCartListId(cartListId);
		cart.setItemId(itemId);
		cart.setQuantity(quantity);
		cart.setHold(false);

		cartService.saveOrUpdateCart(cart); // ★追加

		return "redirect:/voidrshop/cart";
	}

	// ================================
	// ★変更：削除アクション（本人所有チェック版を呼ぶ）
	// ================================

	/** ★この商品のみ削除（本人チェック版） */
	@PostMapping("/item/{cartId}/delete")
	public String deleteItem(@PathVariable long cartId,
			@RequestParam long cartListId,
			@AuthenticationPrincipal LoginUser loginUser) // ★変更
	{
		cartService.deleteItemSecured(loginUser.getId(), cartId, cartListId); // ★変更
		return "redirect:/voidrshop/cart";
	}

	/** ★このカートを空にする（本人チェック版） */
	@PostMapping("/clear")
	public String clearCart(@RequestParam long cartListId,
			@AuthenticationPrincipal LoginUser loginUser) // ★変更
	{
		cartService.clearMyCartSecured(loginUser.getId(), cartListId); // ★変更
		return "redirect:/voidrshop/cart";
	}
	
	// 追加★
	@PostMapping(value = "/add-api", produces = MediaType.APPLICATION_JSON_VALUE) // ★追加
	public ResponseEntity<Map<String, Object>> addToCartApi
	(
			@RequestParam long itemId,
			@RequestParam(defaultValue = "1") int quantity,
			@AuthenticationPrincipal LoginUser loginUser) {
		long cartListId = cartService.ensureCartListId(loginUser.getId(), true);

		Cart cart = new Cart();
		cart.setCartListId(cartListId);
		cart.setItemId(itemId);
		cart.setQuantity(quantity);
		cart.setHold(false);

		cartService.saveOrUpdateCart(cart);

		// 必要なら合計点数など返却してもOK
		return ResponseEntity.ok(Map.of(
				"ok", true,
				"message", "商品をカートに追加しました"));
	}
}
