package com.example.voidr.view;

import com.example.voidr.entity.Cart;
import com.example.voidr.entity.Item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartView
{
	private Cart cart;
	private Item item;
}
