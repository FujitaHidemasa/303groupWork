package com.example.voidr.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.voidr.entity.Item;
import com.example.voidr.service.ItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/voidrshop")
public class TopController
{
	private final ItemService itemService;
	/**
	 * トップ画面を表示する
	 */
	@GetMapping
	public String showMenu(Model model)
	{
		itemService.syncItems();
		// 1~4の
		List<Item> items = itemService.getItemsByRangeId(1, 4);
		model.addAttribute("items", items);
		return "shop/top";
	}
}
