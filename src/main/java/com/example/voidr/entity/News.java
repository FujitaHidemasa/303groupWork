package com.example.voidr.entity;

import java.time.LocalDate;

import lombok.Data;

/**
 * 新着情報:エンティティ
 */
@Data
public class News {
    private Integer id;
    private LocalDate newsDate;
    private String content;
}