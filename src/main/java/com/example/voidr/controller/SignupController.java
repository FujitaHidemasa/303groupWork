package com.example.voidr.controller;

import jakarta.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.voidr.entity.Role;
import com.example.voidr.form.SignupForm;
import com.example.voidr.service.AccountService;

import lombok.RequiredArgsConstructor;

// 10/27 谷口 作成

@Controller
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignupController {

    private final AccountService accountService;

    // ★追加：前後空白トリム＆空白のみ入力は null に変換
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true)); // true => ""や空白だけをnullに
    }

    @GetMapping
    public String showForm(@ModelAttribute("form") SignupForm form) {
        return "shop/signup";
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute("form") SignupForm form,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "shop/signup";
        }

        if (accountService.findByUsername(form.getUsername()) != null) {
            bindingResult.rejectValue("username", "duplicate", "そのユーザー名は既に使用されています");
            return "shop/signup";
        }

        // ★変更：address / phoneNumber も渡す
        accountService.registerAccount(
            form.getUsername(),
            form.getPassword(),
            Role.USER, // com.example.voidr.entity.Roleインポート
            form.getDisplayName(),
            form.getEmail(),
            form.getAddress(),       // ★追加
            form.getPhoneNumber()    // ★追加
        );

        return "redirect:/login?registered";
    }
}
