package com.example.voidr.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.voidr.entity.LoginUser;
import com.example.voidr.service.CartService;
import com.example.voidr.service.ItemService;
import com.example.voidr.view.CartView;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/voidrshop/cart")
@RequiredArgsConstructor
public class CartController {
	
	private final ItemService itemService;

    private final CartService cartService;

    /**
     * カート一覧ページ
     */
    @GetMapping
    public String cartList(Model model, @AuthenticationPrincipal LoginUser loginUser) {
    	
    	itemService.syncItems();
    

        // カートと商品情報をまとめたCartViewリストを取得
        List<CartView> cartViews = cartService.getCartViewByUserId(loginUser.getId());

        // is_hold = true / false に分ける
        List<CartView> holdItems = cartViews.stream()
                .filter(c -> c.getCart().isHold())
                .toList();

        List<CartView> normalItems = cartViews.stream()
                .filter(c -> !c.getCart().isHold())
                .toList();

        model.addAttribute("holdItems", holdItems);
        model.addAttribute("normalItems", normalItems);
        
        // Thymeleaf テンプレート名
        return "shop/cart/list";
    }
    

}
