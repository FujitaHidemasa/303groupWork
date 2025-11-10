package com.example.voidr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.voidr.entity.Favorite;
import com.example.voidr.service.FavoriteService;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {
	
	@Autowired
	private FavoriteService favoriteService;  // ← ✅ スペル修正（fovarite → favorite）
	
	
	/** お気に入り一覧ページ */
	@GetMapping("/{userId}")
	public String showFavorites(@PathVariable Long userId, Model model) {
		// ❌ FavoriteService.getFavoritesByUser() → ✅ favoriteService.getFavoritesByUser()
		List<Favorite> favorites = favoriteService.getFavoritesByUser(userId);
		model.addAttribute("favorites", favorites);
		return "favorite/list"; // ← Thymeleaf テンプレート（例：templates/favorite/list.html）
	}
	
	
	/** お気に入り追加 */
	@PostMapping("/add/{userId}/{itemId}")
	public String addFavorite(@PathVariable Long userId, @PathVariable Long itemId) {
		favoriteService.addFavorite(userId, itemId);
		return "redirect:/favorites/" + userId;
	}
	
	
	/** お気に入り削除 */
	@PostMapping("/remove/{userId}/{itemId}")
	public String removeFavorite(@PathVariable Long userId, @PathVariable Long itemId) {
		favoriteService.removeFavorite(userId, itemId);
		return "redirect:/favorites/" + userId;
	}
	
	
	@PostMapping("/toggle/{userId}/{itemId}")
	@ResponseBody
	public String toggleFavorite(@PathVariable Long userId, @PathVariable Long itemId) {
		boolean isNowFavorite = favoriteService.toggleFavorite(userId, itemId);
		// 戻り値として現在のお気に入り状態を返す（JavaScriptでボタンの表示切替に使用）
		return isNowFavorite ? "favorite" : "not_favorite";
	}
	
	// ★追加: /favorites と /favorites/ を受ける
	@GetMapping({ "", "/" })
	public String myFavorites(
			@org.springframework.security.core.annotation.AuthenticationPrincipal com.example.voidr.entity.LoginUser loginUser) {
		if (loginUser == null) {
			return "redirect:/login";
		}
		return "redirect:/favorites/" + loginUser.getId();
	}
}
