package com.example.voidr.entity;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

/*
 * お届け先住所用　エンティティ
 */

@Data
public class Address {
    private Long id;
    private Long userId;
    
    @NotBlank(message ="受取人名を入力して下さい")
    @Size(max = 50, message = "受取人名は50文字以内で入力して下さい")
    private String recipientName;
    
    @NotBlank(message ="郵便番号を入力して下さい")
    @Pattern(regexp = "\\d{3}-\\d{4}", message ="郵便番号はxxx-xxxxの形式で入力して下さい")
    private String postalCode;
    
    @NotBlank(message ="住所を入力してください")
    @Size(max = 200, message ="住所は200文字以内で入力して下さい")
    private String address;
    
    @NotBlank(message ="電話番号を入力して下さい")
    @Pattern(regexp ="0\\d{2}-\\d{4}-\\d{4}", message ="電話番号はxxx-xxxx-xxxxの形式で入力して下さい")
    private String phone;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
