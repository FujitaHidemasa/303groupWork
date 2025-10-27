package com.example.voidr.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.example.voidr.entity.Item;
import com.example.voidr.service.ItemService;

import lombok.RequiredArgsConstructor;

/**
 * アイテムのController
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/voidrshop/items")
public class ItemController
{
	private final ItemService itemService;

	/** 一覧ページ */
	@GetMapping
	public String list(Model model)
	{
		// XML ↔ DB 同期
		itemService.syncItems();

		// DBから一覧取得
		List<Item> items = itemService.getAllItems();
		model.addAttribute("items", items);
		return "shop/item/list";
	}
	
	@GetMapping("/search")
	public String search(@RequestParam("keyword") String keyword, Model model)
	{
		itemService.syncItems();
		
	    List<Item> items = itemService.searchItemsByKeyword(keyword);
	    model.addAttribute("items", items);
	    model.addAttribute("keyword", keyword);
	    return "shop/item/list";
	}

	/** 詳細ページ */
	@GetMapping("/{id}")
	public String detail(@PathVariable("id") Long id, Model model)
	{
		Item item = itemService.getItemById(id);
		if(item == null || item.getId() <= 0)
		{
			new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
		}
		model.addAttribute("item", item);
		return "shop/item/detail";
	}
}
