package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.Item;

public interface ItemService
{
    /**
     * XMLとDBを同期する
     */
    void syncItems();

    /**
     * 全ての商品を取得し、カテゴリもセットして返す
     * @return List<Item>
     */
    List<Item> getAllItems();
}
