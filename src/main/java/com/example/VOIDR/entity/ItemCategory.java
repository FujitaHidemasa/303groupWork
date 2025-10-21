package com.example.VOIDR.entity;

public enum ItemCategory
{
	DOWNLOAD("ダウンロード"),
	BOOKS("本"),
	DOLL("人形"),
	CLOTHING("衣類"),
	BEAUTY("美容");

	private final String displayName;

	ItemCategory(String displayName)
	{
        this.displayName = displayName;
    }

	public String getDisplayName()
	{
		return displayName;
	}
}