package com.example.voidr.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item
{
	/** 商品ID */
	private long id;
	
	/** 商品名 */
	private String name;
	
	/** 値段 */
	private int price;
	
	/** 概要 */
	private String overview;
	
	/** ダウンロード商品か */
	private boolean isDownload;
	
	/** 作成日 */
	private LocalDateTime createdAt;
	
	/** 更新日 */
	private LocalDateTime updatedAt;
}
