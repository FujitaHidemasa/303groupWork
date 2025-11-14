	package com.example.voidr.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.voidr.entity.Address;
import com.example.voidr.repository.AddressMapper;
import com.example.voidr.service.AddressService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    public List<Address> getAddressesByUserId(Long userId) {
        return addressMapper.findByUserId(userId);
    }

    @Transactional
    @Override
    public void addAddress(Address address) {
        addressMapper.insertAddress(address);
    }

    @Transactional
    @Override
    public void deleteAddress(Long id) {
        addressMapper.deleteAddress(id);
    }
}

