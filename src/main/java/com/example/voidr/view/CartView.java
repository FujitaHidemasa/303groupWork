package com.example.voidr.view;

import com.example.voidr.entity.Cart;
import com.example.voidr.entity.Item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 画面表示用：Cart + Item の入れ子 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartView 
{
    private Cart cart;
    private Item item;
	public int getSubtotal() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}
}
