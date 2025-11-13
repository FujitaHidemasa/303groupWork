package com.example.voidr.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @GetMapping
    public String showOrders(Model model) {
        model.addAttribute("pageTitle", "注文管理");
        return "admin/orders";
    }
}
