package com.example.voidr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
	public String showMenu()
	{
		itemService.syncItems();
		// templatesフォルダ配下のmenu.htmlに遷移
		return "shop/top";
	}
	
}
