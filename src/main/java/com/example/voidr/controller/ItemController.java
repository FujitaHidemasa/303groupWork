package com.example.voidr.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // ★追加
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.example.voidr.entity.Item;
import com.example.voidr.entity.LoginUser; // ★追加 11/06
import com.example.voidr.service.FavoriteService; // ★追加 11/06
import com.example.voidr.service.ItemService;

import lombok.RequiredArgsConstructor;

/**
 * アイテムのController
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/voidrshop/items")
public class ItemController {

	private final ItemService itemService;
	private final FavoriteService favoriteService; // ★追加 11/06

	/** 一覧ページ（/voidrshop/items） */
	@GetMapping
	public String list(Model model) {
		itemService.syncItems();
		List<Item> items = itemService.getAllItems();
		model.addAttribute("items", items);
		model.addAttribute("keyword", "");
		return "shop/item/list";
	}

	/** 検索（/voidrshop/items/search?keyword=...） */
	@GetMapping("/search")
	public String search(@RequestParam("keyword") String keyword, Model model) {
		if (keyword == null || keyword.isBlank()) {
			return "redirect:/voidrshop";
		}
		itemService.syncItems();
		List<Item> items = itemService.searchItemsByKeyword(keyword);
		model.addAttribute("items", items);
		model.addAttribute("keyword", keyword);
		return "shop/item/list";
	}

	/** 詳細ページ（/voidrshop/items/{id}） */
	@GetMapping("/{id}")
	public String detail(@PathVariable("id") Long id,
			@AuthenticationPrincipal LoginUser loginUser, // ★追加：ログインユーザー取得
			Model model) {
		Item item = itemService.getItemById(id);
		if (item == null) { // ★安全側：nullのみ判定（id<=0判定は不要）
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
		}
		model.addAttribute("item", item);

		// ★追加 11/06：初期表示用「お気に入り状態」
		boolean isFavorite = false;
		if (loginUser != null) {
			isFavorite = favoriteService.isFavorite(loginUser.getId(), id);
		}
		model.addAttribute("isFavorite", isFavorite);

		return "shop/item/detail";
	}

	/** ★追加 11/06：お気に入りトグルAPI（/voidrshop/items/{itemId}/favorite） */
	@PostMapping("/{itemId}/favorite")
	@ResponseBody
	public boolean toggleFavorite(@PathVariable("itemId") Long itemId,
			@AuthenticationPrincipal LoginUser loginUser) {
		if (loginUser == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
		}
		return favoriteService.toggleFavorite(loginUser.getId(), itemId);
	}
}
