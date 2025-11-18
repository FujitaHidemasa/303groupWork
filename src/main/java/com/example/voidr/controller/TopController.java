package com.example.voidr.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.voidr.entity.Item;
import com.example.voidr.entity.News;
import com.example.voidr.service.ItemService;
import com.example.voidr.service.NewsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/voidrshop")
public class TopController
{
	private final ItemService itemService;
	private final NewsService newsService;
	/**
	 * トップ画面を表示する
	 */
	@GetMapping
	public String showMenu(Model model)
	{
		/** 削除した商品を復活させるためコメントアウト */
	     // itemService.syncItems();

	    // ランダム4件を取得
	    List<Item> items = itemService.getRandom4Items();

	    // items にセットしてHTMLへ渡す
	    model.addAttribute("items", items);
	    
	    // 新着情報（最新3件）を取得してモデルへ
	    List<News> newsList = newsService.getLatest3();
	    model.addAttribute("newsList", newsList);

	    return "shop/top";
	}

}
