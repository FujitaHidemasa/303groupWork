package com.example.voidr.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping
    public String showNews(Model model) {
    	model.addAttribute("newsList", newsService.findAll());
        model.addAttribute("pageTitle", "新着情報管理");
        return "admin/news";
    }
}
