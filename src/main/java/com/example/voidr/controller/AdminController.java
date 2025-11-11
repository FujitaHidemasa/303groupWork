package com.example.voidr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 管理者ページ用コントローラー
 * 
 * ダッシュボード（仮）を表示するだけの最小構成
 */

@Controller
public class AdminController {

    /** 管理ダッシュボード（トップ） */
    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "管理ダッシュボード");
        return "admin/pre_dashboard"; // templates/admin/pre_dashboard.html を返す
    }

    /** 新着情報管理 */
    @GetMapping("/admin/news")
    public String news(Model model) {
        model.addAttribute("pageTitle", "新着情報管理");
        return "admin/news";
    }

    /** 商品管理 */
    @GetMapping("/admin/items")
    public String items(Model model) {
        model.addAttribute("pageTitle", "商品管理");
        return "admin/items";
    }

    /** 注文管理 */
    @GetMapping("/admin/orders")
    public String orders(Model model) {
        model.addAttribute("pageTitle", "注文管理");
        return "admin/orders";
    }

    /** 会員管理 */
    @GetMapping("/admin/users")
    public String users(Model model) {
        model.addAttribute("pageTitle", "会員管理");
        return "admin/users";
    }

    /** お問い合わせ管理 */
    @GetMapping("/admin/contacts")
    public String contacts(Model model) {
        model.addAttribute("pageTitle", "お問い合わせ管理");
        return "admin/contacts";
    }
}
