package com.example.voidr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/voidr")
    public String homepage() {
        // templates/Homepage.html を返す
        return "Homepage";
    }
}

