package com.example.voidr.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.voidr.entity.PasswordResetPin;

@Mapper
public interface PasswordResetPinMapper 
{
	int insert(PasswordResetPin rec);

	PasswordResetPin findActive
			(@Param("email") String email, 
			 @Param("pin") String pin);

	int consume(@Param("id") Long id);

	int countIssuedRecently
			(@Param("email") String email,
			 @Param("minutes") int minutes);
}
