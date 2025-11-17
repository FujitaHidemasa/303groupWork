package com.example.voidr.controller.admin;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.voidr.entity.Item;
import com.example.voidr.service.ItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/items")  // 商品管理用URL
@RequiredArgsConstructor        //  コンストラクタ注入（Lombok）
public class AdminItemController {
	
    /** 11/13修正（谷口）Mapper ではなく Service を注入 */
    private final ItemService itemService;

	 /** 商品管理画面（一覧） */
    @GetMapping
    public String showItems(Model model) {
    	
    	//データベースから全商品を取得
    	List<Item> items = itemService.getAllItems();
    	
    	// 取得した商品をセット
        model.addAttribute("items", items);
        model.addAttribute("pageTitle", "商品管理");  // ページタイトル
        return "admin/items";  // resources/templates/admin/items.html を表示
    }
    
	/** 新規商品フォーム表示 */
	@GetMapping("/new")
	public String showNewForm(Model model) {

		Item item = new Item();
		// DL商品フラグのデフォルト
		item.setIsDownload(false);

		model.addAttribute("item", item);
		model.addAttribute("pageTitle", "商品新規登録");
		return "admin/item_new";
	}

	/** 新規商品登録処理 */
	@PostMapping("/new")
	public String createItem(
			@RequestParam("name") String name,
			@RequestParam("price") Integer price,
			@RequestParam(name = "overview", required = false) String overview,
			@RequestParam(name = "isDownload", defaultValue = "false") boolean isDownload,
			@RequestParam(name = "thumbsImageName", required = false) String thumbsImageName,
			RedirectAttributes redirectAttributes) {

		// 簡易バリデーション
		if (name == null || name.isBlank()) {
			redirectAttributes.addFlashAttribute("errorMessage", "商品名を入力してください。");
			return "redirect:/admin/items/new";
		}
		if (price == null || price < 0) {
			redirectAttributes.addFlashAttribute("errorMessage", "価格には0以上の数値を入力してください。");
			return "redirect:/admin/items/new";
		}

		Item item = new Item();
		item.setName(name);
		item.setPrice(price);
		item.setOverview(overview);
		item.setIsDownload(isDownload);
		item.setThumbsImageName(thumbsImageName);

		// ID を採番（既存IDの最大値 + 1）
		List<Item> all = itemService.getAllItems();
		Long maxId = all.stream()
				.map(Item::getId)
				.max(Comparator.naturalOrder())
				.orElse(0L);
		item.setId(maxId + 1);

		// 作成日時・更新日時
		LocalDateTime now = LocalDateTime.now();
		item.setCreatedAt(now);
		item.setUpdatedAt(now);

		// カテゴリ・画像は未対応なので空リストでNPE回避
		item.setCategoryList(Collections.emptyList());
		item.setImagesName(Collections.emptyList());

		itemService.createItem(item);

		redirectAttributes.addFlashAttribute("successMessage", "商品を登録しました。");
		return "redirect:/admin/items";
	}

	/** 商品編集フォーム表示 */
	@GetMapping("/edit/{id}")
	public String showEditForm(
			@PathVariable("id") Long id,
			Model model,
			RedirectAttributes redirectAttributes) {

		Item item = itemService.getItemById(id);
		if (item == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "指定した商品が見つかりません。");
			return "redirect:/admin/items";
		}

		model.addAttribute("item", item);
		model.addAttribute("pageTitle", "商品編集");
		return "admin/item_edit";
	}

	/** 商品編集の保存 */
	@PostMapping("/edit/{id}")
	public String updateItem(
			@PathVariable("id") Long id,
			@RequestParam("name") String name,
			@RequestParam("price") Integer price,
			@RequestParam(name = "overview", required = false) String overview,
			@RequestParam(name = "isDownload", defaultValue = "false") boolean isDownload,
			@RequestParam(name = "thumbsImageName", required = false) String thumbsImageName,
			RedirectAttributes redirectAttributes) {

		Item item = itemService.getItemById(id);
		if (item == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "指定した商品が見つかりません。");
			return "redirect:/admin/items";
		}

		// 簡易バリデーション
		if (name == null || name.isBlank()) {
			redirectAttributes.addFlashAttribute("errorMessage", "商品名を入力してください。");
			return "redirect:/admin/items/edit/" + id;
		}
		if (price == null || price < 0) {
			redirectAttributes.addFlashAttribute("errorMessage", "価格には0以上の数値を入力してください。");
			return "redirect:/admin/items/edit/" + id;
		}

		// 変更する項目だけ上書き（カテゴリや画像リストはそのまま保持）
		item.setName(name);
		item.setPrice(price);
		item.setOverview(overview);
		item.setIsDownload(isDownload);
		item.setThumbsImageName(thumbsImageName);
		item.setUpdatedAt(LocalDateTime.now());

		itemService.updateItem(item);

		redirectAttributes.addFlashAttribute("successMessage", "商品情報を更新しました。");
		return "redirect:/admin/items";
	}

	/** 商品削除 */
	@PostMapping("/delete/{id}")
	public String deleteItem(
			@PathVariable("id") Long id,
			RedirectAttributes redirectAttributes) {

		itemService.deleteItem(id);
		redirectAttributes.addFlashAttribute("successMessage", "商品を削除しました。");
		return "redirect:/admin/items";
	}
	
	/**
	 * 【開発用】XML(ItemList.xml) から商品情報をDBへ再取り込みする
	 * 
	 * ※通常運用では使わない想定。
	 * 　DBを作り直したときや、初期セットアップ時に1回だけ叩く。
	 */
//	@GetMapping("/dev-sync")
//	public String devSyncFromXml(RedirectAttributes redirectAttributes) {
//
//		// XML -> DB 同期（ItemServiceImpl.syncItems()）
//		itemService.syncItems();
//
//		redirectAttributes.addFlashAttribute("successMessage", "XMLから商品情報を再読み込みしました。");
//		return "redirect:/admin/items";
//	}
}
