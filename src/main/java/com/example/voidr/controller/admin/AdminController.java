package com.example.voidr.controller.admin;

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

}

