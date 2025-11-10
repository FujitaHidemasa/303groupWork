package com.example.voidr.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.voidr.entity.Account;
import com.example.voidr.entity.Address;
import com.example.voidr.service.AccountService;
import com.example.voidr.service.AddressService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final AccountService accountService;
    private final AddressService addressService; // ✅ 追加

    /** マイページTOP */
    @GetMapping
    public String showMyPage(Model model, Authentication auth, HttpServletRequest request) {
        if (auth == null) return "redirect:/login";

        String username = auth.getName();
        Account account = accountService.findByUsername(username);

        model.addAttribute("account", account);
        model.addAttribute("currentPath", request.getRequestURI());
        return "myPage/mypage";
    }

    
    
    
    /** 会員情報編集ページ */
    @GetMapping("/edit")
    public String editAccount(Model model, Authentication auth, HttpServletRequest request) {
        if (auth == null) return "redirect:/login";

        String username = auth.getName();
        Account account = accountService.findByUsername(username);

        model.addAttribute("account", account);
        model.addAttribute("currentPath", request.getRequestURI());
        return "myPage/editMember";
    }

    /** 更新処理 */
    @PostMapping("/edit/submit")
    public String updateAccount(
            @Valid @ModelAttribute("account") Account formAccount,
            BindingResult bindingResult,
            Authentication auth,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("account", formAccount);
            return "myPage/editMember";
        }

        accountService.updateAccount(auth.getName(), formAccount);
        return "redirect:/mypage/edit?success";
    }

    
    
    
    
    /** ✅ お届け先管理ページ */
    @GetMapping("/address")
    public String showAddressPage(Model model, Authentication auth) {
        if (auth == null) return "redirect:/login";

        String username = auth.getName();
        Account user = accountService.findByUsername(username);

        List<Address> addresses = addressService.getAddressesByUserId(user.getId());
        model.addAttribute("addresses", addresses);
        model.addAttribute("newAddress", new Address());
        return "myPage/address";
    }

    /** お届け先追加 */
    @PostMapping("/address/add")
    public String addAddress(@ModelAttribute("newAddress") Address form, Authentication auth) {
        if (auth == null) return "redirect:/login";

        String username = auth.getName();
        Account user = accountService.findByUsername(username);

        form.setUserId(user.getId());
        addressService.addAddress(form);
        return "redirect:/mypage/address";
    }

    /** お届け先削除 */
    @PostMapping("/address/delete")
    public String deleteAddress(@RequestParam("id") Long id) {
        addressService.deleteAddress(id);
        return "redirect:/mypage/address";
    }
    
    
    
    
    
 // 退会確認ページ
 // 🔹 退会理由入力ページ
    @GetMapping("/delete")
    public String showDeletePage(Model model, Authentication auth) {
        if (auth == null) return "redirect:/login";

        model.addAttribute("reasons", List.of(
            "サービス内容に満足できなかった",
            "利用頻度が低い",
            "別アカウントを使いたい",
            "個人情報を削除したい",
            "その他"
        ));
        return "myPage/delete";
    }

    // 🔹 理由入力 → 確認ページへ
    @PostMapping("/delete/confirm")
    public String confirmDelete(
            @RequestParam("reason") String reason,
            Model model,
            Authentication auth) {

        if (auth == null) return "redirect:/login";

        model.addAttribute("reason", reason);
        model.addAttribute("username", auth.getName());
        return "myPage/deleteConfirm";
    }

    // 🔹 確認ページ → 実際に削除
    @PostMapping("/delete/execute")
    public String executeDelete(
            @RequestParam("reason") String reason,
            Authentication auth) {

        if (auth == null) return "redirect:/login";

        String username = auth.getName();
        accountService.deleteAccountByUsername(username);

        // TODO: 必要なら退会理由をログに保存してもOK

        SecurityContextHolder.clearContext();
        return "redirect:/voidr";
    }

    // 🔹 完了画面
    @GetMapping("/delete/complete")
    public String deleteComplete() {
        return "myPage/deleteComplete";
    }


}
