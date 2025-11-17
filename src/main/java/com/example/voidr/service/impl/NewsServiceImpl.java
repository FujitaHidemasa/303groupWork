package com.example.voidr.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.voidr.entity.News;
import com.example.voidr.repository.NewsMapper;
import com.example.voidr.service.NewsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsMapper newsMapper;

    @Override
    public List<News> getLatest3() {
        return newsMapper.findLatest3();
    }

    @Override
    public void insert(News news) {

        // newsDate が null の場合は今日の日付をセット
        if (news.getNewsDate() == null) {
            news.setNewsDate(LocalDate.now());
        }

        newsMapper.insert(news);
    }
    
    @Override
    public List<News> findAll() {
        return newsMapper.findAll();
    }
    
	// ★追加：ID指定で1件取得（編集画面などで使用可能）
	@Override
	public News findById(Integer id) {
		return newsMapper.findById(id);
	}

	// ★追加：1件更新
	@Override
	public void update(News news) {

		// 日付未設定なら今日を補完（insertと同じポリシー）
		if (news.getNewsDate() == null) {
			news.setNewsDate(LocalDate.now());
		}

		newsMapper.update(news);
	}

	// ★追加：1件削除
	@Override
	public void delete(Integer id) {
		newsMapper.delete(id);
	}
}