package com.example.voidr.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.voidr.entity.Cart;

public interface CartService {

	List<Cart> findByCartListId(long cartListId);

	void insertCart(Cart cart);

	void updateItemCount(long id, int itemCount, LocalDateTime updatedAt);

	void deleteItem(long id);
}
