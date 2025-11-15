package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.News;

public interface NewsService {
    /**
     * 最新の新着情報を3件取得する
     */
    List<News> getLatest3();

    /**
     * 新着情報を1件追加する
     * 日付が null の場合は自動で今日の日付を補完する
     */
    void insert(News news);
    
    /**
     * 全ニュースを新しい順で取得（管理画面用）
     */
    List<News> findAll();
    
	// ★追加：ID指定で1件取得（編集用）
	News findById(Integer id);

	// ★追加：1件更新
	void update(News news);

	// ★追加：1件削除
	void delete(Integer id);
}