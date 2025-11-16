package com.example.voidr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 月別売上一覧用のDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySales {

	/** "2025-11" のような形式の年月文字列 */
	private String yearMonth;

	/** 該当月の売上合計（final_total 合計） */
	private int totalSales;

	/** 該当月の出荷済み注文件数 */
	private int orderCount;
}
