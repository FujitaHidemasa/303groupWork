package com.example.voidr.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PurchaseReceiptDto 
{

    /** 注文番号 */
    private Long orderId;

    /** 注文日時 */
    private LocalDateTime orderDateTime;

    /** ログインユーザーの表示名（挨拶用） */
    private String displayName;

    /** お届け先氏名（/mypage/address の recipientName） */
    private String recipientName;

    /** お届け先郵便番号（/mypage/address の postalCode） */
    private String postalCode;

    /** お届け先住所 */
    private String address;

    /** お届け先電話番号 */
    private String phoneNumber;

    /** 支払い方法 */
    private String paymentMethod;

    /** 商品小計 */
    private int subtotal;

    /** 送料 */
    private int shippingFee;

    /** 合計金額 */
    private int finalTotal;

    /** 注文商品の明細リスト */
    private List<Item> items;

    @Data
    @AllArgsConstructor
    public static class Item {
        private String itemName;
        private int unitPrice;
        private int quantity;
        private int subtotal;
    }
}
