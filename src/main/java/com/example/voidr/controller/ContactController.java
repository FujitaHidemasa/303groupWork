package com.example.voidr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.voidr.form.ContactForm;

@Controller
public class ContactController {

	@GetMapping("/contact")
	public String showContactForm(Model model) {
		// お問い合わせフォーム用のオブジェクトを渡す
		model.addAttribute("contactForm", new ContactForm());
		return "contact"; // templates/contact.html を表示
	}
}
