package com.example.voidr.entity;

public enum Role
{
	ADMIN("ROLE_ADMIN"), USER("ROLE_USER"), GUEST("ROLE_GUEST");
	
	private String name;
	
	private Role(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
}

