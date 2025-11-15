package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.voidr.entity.News;

@Mapper
public interface NewsMapper {
	
    /**
     * 最新のニュースを上から3件取得する。
     * @return 最新3件のニュースリスト（idの降順で並ぶ）
     */
    List<News> findLatest3();
    
    /**
     * ニュースを1件追加する。
     * 日付（newsDate）がnullの場合は、Service側で今日の日付に補完される。
     * @param news 追加対象の新着情報（newsDate と content）
     */
    void insert(News news);
    
    /**
     * 全てのニュースを新しい順に取得する（管理画面用）。
     * @return 全ニュースリスト（id の降順で並ぶ）
     */
    List<News> findAll();
    
	// ★追加：ID指定で1件取得
	News findById(Integer id);

	// ★追加：1件更新
	void update(News news);

	// ★追加：1件削除
	void delete(Integer id);
}
