package com.example.voidr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 10/23 谷口
 * 新規登録ページの表示を担当するコントローラ。
 * 
 * GETリクエスト /signup に対して
 * templates/security/signup.html を返す。
 */
@Controller
public class SignupController 
{

	// /sigunup にアクセスしたときに signup.html を返す
    @GetMapping("/signup")
    public String signup() 
    {
        // templates/security/signup.html を表示
        return "security/signup";
    }
}
