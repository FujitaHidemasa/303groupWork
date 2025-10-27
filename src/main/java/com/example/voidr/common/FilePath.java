package com.example.voidr.common;

import java.io.File;

/**
 * ファイルの場所(相対パス)を表すenum
 */
public enum FilePath {

	// ItemList.xmlの場所
	ITEM_LIST_XML("src/main/resources/items/ItemList.xml");

	private final String relativePath;

	private FilePath(String relativePath) {
		this.relativePath = relativePath;
	}

	/** 相対パスを取得 */
	public String getRelativePath() {
		return relativePath;
	}
	
	public File getPathFile()
	{
		return new File(relativePath);
	}

	/** ファイルやフォルダが存在するかチェック */
	public boolean exists() {
		return new File(relativePath).exists();
	}

	@Override
	public String toString() {
		return relativePath;
	}
}
