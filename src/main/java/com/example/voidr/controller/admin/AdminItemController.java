package com.example.voidr.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.voidr.entity.Item;
import com.example.voidr.repository.ItemCategoryMapper;
import com.example.voidr.repository.ItemMapper;

@Controller
@RequestMapping("/admin/items")  // 商品管理用URL
public class AdminItemController {
	
	 private final ItemMapper itemMapper;
	 private final ItemCategoryMapper itemCategoryMapper;

	 public AdminItemController(ItemMapper itemMapper, ItemCategoryMapper itemCategoryMapper) {
	        this.itemMapper = itemMapper;
	        this.itemCategoryMapper = itemCategoryMapper;
	    }

    @GetMapping
    public String showItems(Model model) {
    	
    	//データベースから全商品を取得
    	List<Item> items = itemMapper.selectAll();
    	
    	// 取得した商品をセット
        model.addAttribute("items", items);
        model.addAttribute("pageTitle", "商品管理");  // ページタイトル
        return "admin/items";  // resources/templates/admin/items.html を表示
    
    }
    
 
}
