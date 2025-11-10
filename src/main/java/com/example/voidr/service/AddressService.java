package com.example.voidr.service;

import java.util.List;

import com.example.voidr.entity.Address;

public interface AddressService {
    List<Address> getAddressesByUserId(Long userId);
    void addAddress(Address address);
    void deleteAddress(Long id);
}
