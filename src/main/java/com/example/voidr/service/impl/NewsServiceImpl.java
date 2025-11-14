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
}