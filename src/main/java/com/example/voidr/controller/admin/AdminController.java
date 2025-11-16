package com.example.voidr.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.voidr.dto.MonthlySales;
import com.example.voidr.service.OrderListService; // ★追加

import lombok.RequiredArgsConstructor; // ★追加

/**
 * 管理者ページ用コントローラー
 * 
 * ダッシュボード表示
 */
@Controller
@RequiredArgsConstructor // ★追加：final フィールドのコンストラクタを自動生成
public class AdminController {

	// 注文リストサービス（売上集計に使用）
	private final OrderListService orderListService;

	/** 管理ダッシュボード（トップ） */
	@GetMapping("/admin")
	public String dashboard(Model model) {

		// 当月売上（status = 'SHIPPED' の final_total 合計）
		int currentMonthSales = orderListService.getCurrentMonthSales();

		model.addAttribute("currentMonthSales", currentMonthSales);
		model.addAttribute("pageTitle", "管理ダッシュボード");

		// 既存と同じテンプレート名を返す
		return "admin/pre_dashboard"; // templates/admin/pre_dashboard.html を返す
	}
	
	/** 月別売上一覧（過去12ヶ月） */
	@GetMapping("/admin/sales")
	public String showMonthlySales(Model model) {

		// 過去12ヶ月分の月別売上一覧を取得
		List<MonthlySales> salesList = orderListService.getMonthlySalesLast12Months();

		model.addAttribute("salesList", salesList);
		model.addAttribute("pageTitle", "月別売上一覧");
		model.addAttribute("active", "sales");

		return "admin/sales_list"; // 次で作るテンプレート
	}

}
