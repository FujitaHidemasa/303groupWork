package com.example.voidr.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.voidr.entity.Cart;
import com.example.voidr.entity.Order;
import com.example.voidr.entity.OrderItem;
import com.example.voidr.entity.OrderList;
import com.example.voidr.repository.CartMapper;
import com.example.voidr.repository.OrderItemMapper;
import com.example.voidr.repository.OrderListMapper;
import com.example.voidr.repository.OrderMapper;
import com.example.voidr.service.OrderListService;
import com.example.voidr.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderMapper orderMapper;
	private final CartMapper cartMapper;
	private final OrderItemMapper orderItemMapper;
	private final OrderListService orderListService;
	private final OrderListMapper orderListMapper; // 追加

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
		List<Cart> cartItems = cartMapper.findByCartListId(orderListId);
		if (cartItems.isEmpty())
			return;

		for (Cart cart : cartItems) {
			Order order = new Order();
			order.setOrderListId(orderListId);
			order.setItemId(cart.getItemId());
			order.setHold(false);
			orderMapper.insertOrder(order);

			OrderItem orderItem = new OrderItem();
			orderItem.setOrderId(order.getId());
			orderItem.setItemId(cart.getItemId());
			orderItem.setPrice(cart.getItem().getPrice());
			orderItem.setQuantity(cart.getQuantity());
			orderItemMapper.insertOrderItem(orderItem);
		}

		cartMapper.deleteByCartListId(orderListId);
	}

	/**
	 * 既存の cancelOrder メソッドはそのまま残しても OK
	 */
	@Override
	@Transactional
	public void cancelOrder(Long orderListId, String username) {
		// 注文リスト取得
		OrderList orderList = orderListService.findById(orderListId);
		if (orderList == null) {
			throw new IllegalArgumentException("注文リストが存在しません。");
		}

		// キャンセル権限チェック
		if (!orderList.getUsername().equals(username)) {
			throw new IllegalArgumentException("この注文をキャンセルする権限がありません。");
		}

		// ステータスチェック
		if (!"NEW".equals(orderList.getStatus())) {
			throw new IllegalStateException("この注文はキャンセルできません。");
		}

		// 注文リストステータス更新のみ（Order個別は変更しない）
		orderListService.updateStatus(orderList.getId(), "CANCELLED");
	}

}
