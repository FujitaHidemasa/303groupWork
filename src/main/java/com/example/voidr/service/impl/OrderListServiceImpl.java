package com.example.voidr.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.voidr.entity.Account;
import com.example.voidr.entity.OrderList;
import com.example.voidr.repository.AccountMapper;
import com.example.voidr.repository.OrderListMapper;
import com.example.voidr.service.OrderListService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderListServiceImpl implements OrderListService {

	private final OrderListMapper orderListMapper;
	private final AccountMapper accountMapper;

	@Override
	public List<OrderList> getOrderListsByUserId(long userId) {
		return orderListMapper.findByUserId(userId);
	}

	@Override
	public void createOrderList(OrderList orderList) {
		// username からユーザー情報を取得
		Account account = accountMapper.selectByUsername(orderList.getUsername());
		if (account == null) {
			throw new IllegalArgumentException("ユーザーが存在しません: " + orderList.getUsername());
		}

		orderList.setUserId(account.getId());

		// 注文リストを登録
		orderListMapper.insertOrderList(orderList);
	}

	@Override
	public OrderList findByUserName(String username) {
		return orderListMapper.findByUserName(username);
	}
}
