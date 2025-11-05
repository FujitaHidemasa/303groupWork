package com.example.voidr.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.voidr.entity.Cart;
import com.example.voidr.entity.Order;
import com.example.voidr.entity.OrderItem;
import com.example.voidr.repository.CartMapper;
import com.example.voidr.repository.OrderItemMapper;
import com.example.voidr.repository.OrderMapper;
import com.example.voidr.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderMapper orderMapper;
	private final CartMapper cartMapper; // ✅ 追加
	private final OrderItemMapper orderItemMapper; // ✅ 追加

	@Override
	public List<Order> getOrderHistory(long orderListId) {
		return orderMapper.findByOrderListId(orderListId);
	}

	@Override
	@Transactional
	public void createOrder(Order order) {
		orderMapper.insertOrder(order);
	}

	@Override
	@Transactional
	public void confirmPurchase(long orderListId) {
		// カート内商品を取得
		List<Cart> cartItems = cartMapper.findByCartListId(orderListId);

		// カートが空なら終了
		if (cartItems.isEmpty())
			return;

		// 各カート商品を注文として登録
		for (Cart cart : cartItems) {
			Order order = new Order();
			order.setOrderListId(orderListId);
			order.setItemId(cart.getItemId());
			order.setHold(false);
			orderMapper.insertOrder(order);

			// 注文詳細（order_item）登録
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderId(order.getId());
			orderItem.setItemId(cart.getItemId());
			orderItem.setPrice(cart.getItem().getPrice());
			orderItem.setQuantity(cart.getQuantity());
			orderItemMapper.insertOrderItem(orderItem);
		}

		// カートを空にする
		cartMapper.deleteByCartListId(orderListId);
	}
}
