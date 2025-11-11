package com.example.voidr.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.Address;

@Mapper
public interface AddressMapper {

    /** 特定ユーザーの住所一覧取得 */
    List<Address> findByUserId(@Param("userId") Long userId);

    /** 新しい住所を登録 */
    void insertAddress(Address address);

    /** 特定住所を削除 */
    void deleteAddress(@Param("id") Long id);
}
