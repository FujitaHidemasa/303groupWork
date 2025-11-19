package com.example.voidr.controller.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.voidr.entity.Item;
import com.example.voidr.service.ItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/items") // 商品管理用URL
@RequiredArgsConstructor //  コンストラクタ注入（Lombok）
public class AdminItemController {
	/**
	 * サムネイル画像を ./uploads/item-thumbs/ に保存し、
	 * DBに保存するファイル名（storedName）を返す。
	 */
	private String saveThumbImage(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			return null;
		}

		// アップロードディレクトリ（プロジェクト直下）
		Path uploadDir = Paths.get("uploads", "item-thumbs");
		Files.createDirectories(uploadDir);

		// 元ファイル名
		String originalName = file.getOriginalFilename();
		if (originalName == null || originalName.isBlank()) {
			originalName = "thumb";
		}

		// 拡張子を保持したまま一意なファイル名を作成
		String ext = "";
		int dotIndex = originalName.lastIndexOf('.');
		if (dotIndex >= 0) {
			ext = originalName.substring(dotIndex); // 例: ".png"
		}
		String storedName = System.currentTimeMillis() + ext;

		// 保存
		Path dest = uploadDir.resolve(storedName);
		Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

		return storedName;
	}

	private final ItemService itemService;

	/** 商品管理画面（一覧） */
	@GetMapping
	public String showItems(Model model) {

		// ▼通常版（削除済みを除外）
		// List<Item> items = itemService.getAllItems(); 
		// ※不具合時は↑の1行に戻すだけで従来表示に復旧できます。

		// ▼管理者用：削除済み商品も含めて一覧表示
		List<Item> items = itemService.getAllItemsIncludingDeleted();

		// 取得した商品をセット
		model.addAttribute("items", items);
		model.addAttribute("pageTitle", "商品管理"); // ページタイトル
		return "admin/items"; // resources/templates/admin/items.html を表示
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
			@RequestParam(name = "categories", required = false) String categories,
			@RequestParam(name = "thumbsImageName", required = false) String thumbsImageName,
			@RequestParam(name = "thumbsFile", required = false) MultipartFile thumbsFile, // ★追加
			RedirectAttributes redirectAttributes) {

		// ▼ ここは今まで通りのバリデーション（必須チェックなど）
		if (name == null || name.isBlank()) {
			redirectAttributes.addFlashAttribute("errorMessage", "商品名を入力してください。");
			return "redirect:/admin/items/new";
		}
		if (price == null || price < 0) {
			redirectAttributes.addFlashAttribute("errorMessage", "価格は0以上の数値で入力してください。");
			return "redirect:/admin/items/new";
		}

		Item item = new Item();
		item.setName(name);
		item.setPrice(price);
		item.setOverview(overview);
		item.setIsDownload(isDownload);

		// ★カテゴリ
		item.setCategoryList(parseCategories(categories));

		// ★サムネイル画像：ファイルがあればそれを優先、無ければテキスト入力を使用
		try {
			if (thumbsFile != null && !thumbsFile.isEmpty()) {
				String storedName = saveThumbImage(thumbsFile);
				item.setThumbsImageName(storedName);
			} else {
				item.setThumbsImageName(thumbsImageName);
			}
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "サムネイル画像の保存に失敗しました。");
			return "redirect:/admin/items/new";
		}

		// 作成日時・更新日時
		LocalDateTime now = LocalDateTime.now();
		item.setCreatedAt(now);
		item.setUpdatedAt(now);

		// 画像リストは未対応なので空でOK
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
			@RequestParam(name = "categories", required = false) String categories,
			@RequestParam(name = "thumbsImageName", required = false) String thumbsImageName,
			@RequestParam(name = "thumbsFile", required = false) MultipartFile thumbsFile,
			RedirectAttributes redirectAttributes) {

		Item item = itemService.getItemById(id);
		if (item == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "指定された商品が見つかりません。");
			return "redirect:/admin/items";
		}

		item.setName(name);
		item.setPrice(price);
		item.setOverview(overview);
		item.setIsDownload(isDownload);

		// ★カテゴリ更新
		item.setCategoryList(parseCategories(categories));

		// ★サムネイル：新しいファイルがあれば保存して上書き
		try {
			if (thumbsFile != null && !thumbsFile.isEmpty()) {
				String storedName = saveThumbImage(thumbsFile);
				item.setThumbsImageName(storedName);
			} else {
				// フォームのテキスト欄で変更された場合だけ反映
				item.setThumbsImageName(thumbsImageName);
			}
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "サムネイル画像の保存に失敗しました。");
			return "redirect:/admin/items/edit/" + id;
		}

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
		redirectAttributes.addFlashAttribute("successMessage", "商品を販売終了にしました。");
		return "redirect:/admin/items";
	}

	/** 販売再開（is_deleted = FALSE）*/
	@PostMapping("/restore/{id}")
	public String restoreItem(
			@PathVariable("id") Long id,
			RedirectAttributes redirectAttributes) {

		itemService.restoreItem(id);
		redirectAttributes.addFlashAttribute("successMessage", "商品を販売再開しました。");
		return "redirect:/admin/items";
	}

	/** カテゴリ入力（カンマ or 読点区切り）を List<String> に変換 */
	private List<String> parseCategories(String categoriesInput) {
		if (categoriesInput == null || categoriesInput.isBlank()) {
			return Collections.emptyList();
		}
		return Arrays.stream(categoriesInput.split("[,、]"))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.toList();
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
