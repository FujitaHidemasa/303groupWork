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

	// 追加：この行が「販売終了」商品かどうか
	public boolean isItemDeleted() 
	{
		return item != null && Boolean.TRUE.equals(item.getIsDeleted());
	}

	// 追加：小計（販売終了品は 0 円として扱う）
	public int getSubtotal() 
	{
		if (cart == null || item == null) {
			return 0;
		}
		if (isItemDeleted()) {
			return 0;
		}
		return item.getPrice() * cart.getQuantity();
	}
}
