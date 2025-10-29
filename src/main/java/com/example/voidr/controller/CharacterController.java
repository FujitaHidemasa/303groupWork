package com.example.voidr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CharacterController {
	
	@GetMapping("/character")
    public String characterPage() {
        // templates/character.html を返す
        return "charpage";
    }

}
