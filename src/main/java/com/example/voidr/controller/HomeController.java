package com.example.voidr.controller;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.voidr.entity.Contact;
import com.example.voidr.form.ContactForm;
import com.example.voidr.service.ContactService;

@Controller
public class HomeController {

	// コンストラクタインジェクション用フィールド
	private final ContactService contactService;

	// コンストラクタ
	public HomeController(ContactService contactService) {
		this.contactService = contactService;
	}

	@GetMapping("/voidr")
	public String home() {

		return "home/home";

	}

	//ストーリーページに飛ばす
	@GetMapping("/voidr/story")
	public String story() {

		return "home/story";
	}

	//マップページに飛ばす
	@GetMapping("/voidr/map")
	public String map() {

		return "home/map";
	}

	//武器ページに飛ばす
	@GetMapping("/voidr/weapon")
	public String weapon() {

		return "home/weapon";
	}

	//キャラクターページに飛ばす
	@GetMapping("/voidr/character")
	public String characterPage() {

		return "home/char";
	}

	//ニュースページに飛ばす
	@GetMapping("/voidr/news")
	public String news() {

		return "home/news";
	}

	// お問い合わせページ表示（GET）
	@GetMapping("/voidr/contact")
	public String contactForm(Model model) {

		if (!model.containsAttribute("contactForm")) {
			model.addAttribute("contactForm", new ContactForm());
		}
		return "home/contact"; // templates/home/contact.html
	}

	// お問い合わせ送信（POST）
	@PostMapping("/contact/submit")
	public String submitContact(
			@Valid ContactForm contactForm,
			BindingResult binding,
			RedirectAttributes redirectAttributes) {

		if (binding.hasErrors()) {
			// バリデーション NG → 入力画面に戻す
			return "home/contact";
		}
		// フォーム内容をエンティティにコピー
		Contact contact = new Contact();
		contact.setName(contactForm.getName());
		contact.setEmail(contactForm.getEmail());
		contact.setSubject(contactForm.getSubject());
		contact.setMessage(contactForm.getMessage());
		contact.setStatus("NEW");

		// TODO: ログインユーザーと紐づけたい場合はここで userId をセット
		// contact.setUserId(loginUserId);

		contactService.submitContact(contact);
		// リダイレクト先で表示するメッセージ
		redirectAttributes.addFlashAttribute(
				"successMessage",
				"お問い合わせを送信しました。担当者からの返信をお待ちください。");

		// PRGパターン：POST → Redirect → GET
		return "redirect:/voidr/contact";
	}

	//ログインページに飛ばす

}
