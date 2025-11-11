package com.example.voidr.entity;

import java.time.LocalDateTime;

import lombok.Data;

/*
 * お届け先住所用　エンティティ
 */

@Data
public class Address {
    private Long id;
    private Long userId;
    private String recipientName;
    private String postalCode;
    private String address;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
