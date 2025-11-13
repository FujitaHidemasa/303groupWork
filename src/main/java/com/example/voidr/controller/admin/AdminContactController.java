package com.example.voidr.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/contacts")
public class AdminContactController {

    @GetMapping
    public String showContacts(Model model) {
        model.addAttribute("pageTitle", "お問い合わせ管理");
        return "admin/contacts";
    }
}