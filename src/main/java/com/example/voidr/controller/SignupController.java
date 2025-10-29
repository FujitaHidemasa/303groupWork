// SignupController.java
package com.example.voidr.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.authentication.AuthenticationManager;                 // ★追加
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // ★追加
import org.springframework.security.core.Authentication;                            // ★追加
import org.springframework.security.core.context.SecurityContext;                  // ★追加
import org.springframework.security.core.context.SecurityContextHolder;            // ★追加
import org.springframework.security.web.context.HttpSessionSecurityContextRepository; // ★追加
import org.springframework.security.web.context.SecurityContextRepository;         // ★追加
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.voidr.entity.Role;                                             // ★追加（権限指定に使用）
import com.example.voidr.form.SignupForm;
import com.example.voidr.service.AccountService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignupController 
{

    private final AccountService accountService;

    // ★追加：自動ログイン用
    private final AuthenticationManager authenticationManager;

    // ★追加：SecurityContext をセッションへ保存するためのリポジトリ
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    // 既存：前後空白トリム
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public String showForm(@ModelAttribute("form") SignupForm form) 
    {
        return "shop/signup";
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute("form") SignupForm form,
                         BindingResult bindingResult,
                         HttpServletRequest request,                 // ★追加
                         HttpServletResponse response)               // ★追加
    {
        if (bindingResult.hasErrors()) {
            return "shop/signup";
        }

        if (accountService.findByUsername(form.getUsername()) != null) {
            bindingResult.rejectValue("username", "duplicate", "そのユーザー名は既に使用されています");
            return "shop/signup";
        }

        // 1) アカウント登録（権限は USER）
        accountService.registerAccount(
                form.getUsername(),
                form.getPassword(),
                Role.USER,
                form.getDisplayName(),
                form.getEmail(),
                form.getAddress(),
                form.getPhoneNumber()
        );

        // 2) ★ここから自動ログイン
        Authentication authRequest =
                new UsernamePasswordAuthenticationToken(form.getUsername(), form.getPassword());
        Authentication authResult = authenticationManager.authenticate(authRequest);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);

        // セッションへ保存（重要）
        securityContextRepository.saveContext(context, request, response);

        // 3) ★ログイン済みで @RequestMapping("/voidrshop") へ
        return "redirect:/voidrshop";
    }
}
