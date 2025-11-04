package com.example.voidr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.voidr.form.ContactForm;

@Controller
public class HomeController {

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
        model.addAttribute("contactForm", new ContactForm());
        return "home/contact"; // templates/home/contact.html
    }

    // お問い合わせ送信（POST）
    @PostMapping("/contact/submit")
    public String submitContact(ContactForm contactForm, Model model) {
        // ここでメール送信やDB保存処理などを入れられる
        model.addAttribute("successMessage", "お問い合わせを送信しました！");
        model.addAttribute("contactForm", new ContactForm()); // フォームを空にする
        return "home/contact";
    }
    
    //ログインページに飛ばす
  
}

