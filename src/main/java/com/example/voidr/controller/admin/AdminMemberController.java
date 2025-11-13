package com.example.voidr.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 会員管理画面のコントローラー
 */

@Controller
@RequestMapping("/admin/members")
public class AdminMemberController {

    @GetMapping
    public String showMembers(Model model) {
        model.addAttribute("pageTitle", "会員管理");
        return "admin/members"; // templates/admin/members.html
    }
}
