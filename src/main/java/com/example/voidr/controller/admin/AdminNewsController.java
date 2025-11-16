package com.example.voidr.controller.admin;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.voidr.entity.News;
import com.example.voidr.service.NewsService;

import lombok.RequiredArgsConstructor;

/**
 * 新着情報管理画面コントローラー
 */

@Controller
@RequestMapping("/admin/news")
@RequiredArgsConstructor
public class AdminNewsController {
	
	private final NewsService newsService;

	/**
	* 一覧表示
	*/
    @GetMapping
    public String showNews(Model model) {
    	model.addAttribute("newsList", newsService.findAll());
        model.addAttribute("pageTitle", "新着情報管理");
        return "admin/news";
    }
    
	/**
	 * 新規追加
	 */
	@PostMapping("/create")
	public String createNews(
			@RequestParam(name = "newsDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newsDate,
			@RequestParam("content") String content,
			RedirectAttributes redirectAttributes) {

		if (content == null || content.isBlank()) {
			redirectAttributes.addFlashAttribute("errorMessage", "内容を入力してください。");
			return "redirect:/admin/news";
		}

		News news = new News();
		news.setNewsDate(newsDate); // null の場合は Service 側で今日に補完
		news.setContent(content);

		newsService.insert(news);

		redirectAttributes.addFlashAttribute("successMessage", "新着情報を追加しました。");
		return "redirect:/admin/news";
	}

	/**
	 * 編集フォーム表示
	 */
	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable("id") Integer id, Model model,
			RedirectAttributes redirectAttributes) {

		News news = newsService.findById(id);
		if (news == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "指定された新着情報が見つかりません。");
			return "redirect:/admin/news";
		}

		model.addAttribute("news", news);
		model.addAttribute("pageTitle", "新着情報編集");
		return "admin/news_edit";
	}

	/**
	 * 編集の保存
	 */
	@PostMapping("/{id}/update")
	public String updateNews(
			@PathVariable("id") Integer id,
			@RequestParam(name = "newsDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newsDate,
			@RequestParam("content") String content,
			RedirectAttributes redirectAttributes) {

		if (content == null || content.isBlank()) {
			redirectAttributes.addFlashAttribute("errorMessage", "内容を入力してください。");
			return "redirect:/admin/news";
		}

		News news = new News();
		news.setId(id);
		news.setNewsDate(newsDate);
		news.setContent(content);

		newsService.update(news);

		redirectAttributes.addFlashAttribute("successMessage", "新着情報を更新しました。");
		return "redirect:/admin/news";
	}

	/**
	 * 削除
	 */
	@PostMapping("/{id}/delete")
	public String deleteNews(@PathVariable("id") Integer id,
			RedirectAttributes redirectAttributes) {

		newsService.delete(id);
		redirectAttributes.addFlashAttribute("successMessage", "新着情報を削除しました。");
		return "redirect:/admin/news";
	}
}
