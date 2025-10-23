package com.example.voidr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 10/23 谷口
 * ログインページの表示を担当するコントローラ。
 * 
 * GETリクエスト /login に対して
 * templates/security/login.html を返す。
 */
@Controller
public class LoginController 
{

    // /login にアクセスしたときに login.html を返す
    @GetMapping("/login")
    public String login() 
    {
        return "login"; // templates/login.html を表示
    }
}
