package com.example.voidr.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 新着情報管理画面コントローラー
 */

@Controller
@RequestMapping("/admin/news")
public class AdminNewsController {

    @GetMapping
    public String showNews(Model model) {
        model.addAttribute("pageTitle", "新着情報管理");
        return "admin/news";
    }
}
