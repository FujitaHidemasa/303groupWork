package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.dto.MonthlySales;
import com.example.voidr.entity.OrderList;
//findByUserId(), insertOrderList()
@Mapper
public interface OrderListMapper {
    // ユーザーIDで購入履歴一覧を取得
    List<OrderList> findByUserId(@Param("userId") long userId);

    // 新しい購入履歴（注文リスト）を作成
    void insertOrderList(OrderList orderList);

	List<OrderList> findByUserName(@Param("username") String username);

	// 管理画面用：全ユーザー分の注文リスト＋ユーザー名
	List<OrderList> findAllWithUserName();
	
	// 1件取得
	OrderList findById(long id);
	
	// ステータス更新
	void updateStatus(
			@Param("orderListId") long orderListId,
			@Param("status") String status);
	
	// 当月の売上合計（出荷済みのみ）
	Integer findCurrentMonthSales();
	
	// 過去12ヶ月の月別売上（出荷済みのみ）
	List<MonthlySales> findMonthlySalesLast12Months();
}
