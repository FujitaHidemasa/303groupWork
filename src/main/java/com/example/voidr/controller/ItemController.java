package com.example.voidr.controller;

<<<<<<< HEAD
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.voidr.entity.Item;
import com.example.voidr.repository.ItemMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController
{
	/** DI */
	private final ItemMapper itemMapper;

	/**
	 * 「すること」の一覧を表示します。
	 */
	@GetMapping
	public String list(Model model)
	{
		model.addAttribute("items", itemMapper.selectAll());
		return "item/list";
	}

	/**
	 * 指定されたIDの「すること」の詳細を表示します。
	 */
	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model, RedirectAttributes attributes)
	{
		Item item = itemMapper.selectById(id);
		if(item != null)
		{
			model.addAttribute("items", itemMapper.selectById(id));
			return "item/detail";
		}
		else
		{
			attributes.addFlashAttribute("errorMessage", "対象データがありません");
			return "redirect:/items";
		}
=======
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.example.voidr.entity.Item;
import com.example.voidr.service.ItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
	private final ItemService itemService;

	/** 一覧ページ */
	@GetMapping
	public String list(Model model) {
		// XML ↔ DB 同期
		itemService.syncItems();

		// DBから一覧取得
		List<Item> items = itemService.getAllItems();
		model.addAttribute("items", items);
		return "item/list";
	}

	/** 詳細ページ */
	@GetMapping("/{id}")
	public String detail(@PathVariable("id") Long id, Model model) {
		Item item = itemService.getAllItems().stream()
				.filter(i -> i.getId() == id)
				.findFirst()
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
		model.addAttribute("item", item);
		return "item/detail";
>>>>>>> branch 'master' of https://github.com/FujitaHidemasa/303groupWork.git
	}
}
