package com.example.voidr.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser
{
	private Long id;

	private String username;
	
	private String password;
	
	private Role authority;
}
