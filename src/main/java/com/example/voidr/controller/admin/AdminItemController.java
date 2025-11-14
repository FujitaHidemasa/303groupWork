package com.example.voidr.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.voidr.entity.Item;
import com.example.voidr.service.ItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/items")  // 商品管理用URL
@RequiredArgsConstructor        // 11/14追加（谷口）コンストラクタ注入（Lombok）
public class AdminItemController {
	
    /** 11/13修正（谷口）Mapper ではなく Service を注入 */
    private final ItemService itemService;

	 /** 商品管理画面（一覧） */
    @GetMapping
    public String showItems(Model model) {
    	
    	//データベースから全商品を取得
    	List<Item> items = itemService.getAllItems(); // Service経由に変更（谷口）
    	
    	// 取得した商品をセット
        model.addAttribute("items", items);
        model.addAttribute("pageTitle", "商品管理");  // ページタイトル
        return "admin/items";  // resources/templates/admin/items.html を表示
    
    }
}
