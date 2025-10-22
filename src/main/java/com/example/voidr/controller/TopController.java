package com.example.voidr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/voidrshop")
public class TopController
{
	/**
	 * トップ画面を表示する
	 */
	@GetMapping
	public String showMenu()
	{
		// templatesフォルダ配下のmenu.htmlに遷移
		return "shop/top";
	}
}
