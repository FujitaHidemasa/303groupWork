package com.example.voidr.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.example.voidr.entity.Contact;
import com.example.voidr.service.ContactService;

import lombok.RequiredArgsConstructor;
/*
 * お問い合わせ管理コントローラ
 */
@Controller
@RequestMapping("/admin/contacts")
@RequiredArgsConstructor
public class AdminContactController {

    private final ContactService contactService;

    // 一覧表示
    @GetMapping
    public String showContacts(
            @RequestParam(name = "status", required = false) String status,
            Model model) {

        List<Contact> contacts;
        if (status == null || status.isBlank() || "ALL".equals(status)) {
            contacts = contactService.findAll();
            status = "ALL";
        } else {
            contacts = contactService.findByStatus(status);
        }

        model.addAttribute("pageTitle", "お問い合わせ管理");
        model.addAttribute("contacts", contacts);
        model.addAttribute("selectedStatus", status);
        return "admin/contacts";
    }

    // 詳細表示：/admin/contacts/{id}
    @GetMapping("/{id}")
    public String showContactDetail(
            @PathVariable("id") long id,
            Model model) {

        Contact contact = contactService.findById(id);
        if (contact == null) {
            // 存在しないIDなら404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "お問い合わせが見つかりません");
        }

        model.addAttribute("pageTitle", "お問い合わせ詳細");
        model.addAttribute("contact", contact);
        return "admin/contact-detail";
    }
    
	// 1件削除
	@PostMapping("/delete")
	public String deleteContact(@RequestParam("id") long id) {
		contactService.deleteById(id);
		return "redirect:/admin/contacts";
	}

    // ステータス更新（一覧 or 詳細から共通で使用）
    @PostMapping("/updateStatus")
    public String updateStatus(
            @RequestParam("id") long id,
            @RequestParam("status") String status,
            @RequestParam(name = "adminReply", required = false) String adminReply,
            Model model) {

        contactService.updateStatus(id, status, adminReply);

        // 一覧に戻す
        return "redirect:/admin/contacts";
    }
}