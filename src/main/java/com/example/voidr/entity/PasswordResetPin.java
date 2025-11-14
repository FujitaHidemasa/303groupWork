package com.example.voidr.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PasswordResetPin 
{
	private Long id;
	private String email;
	private String pin;
	private LocalDateTime expireAt;
	private boolean consumed;
	private LocalDateTime createdAt;
}
