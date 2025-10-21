package com.example.voidr.controller;

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
	}
}
