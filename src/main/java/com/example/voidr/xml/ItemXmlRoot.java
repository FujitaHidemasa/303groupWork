package com.example.voidr.xml;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * XMLのルート要素「itemList」に対応するクラス。
 * 複数の ItemXmlInfo を保持する。
 */
@Data
@XmlRootElement(name = "itemList")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemXmlRoot
{

	/**
	 * 商品リスト。
	 * XML要素 "item" のリストに対応。
	 */
	@XmlElement(name = "item")
	private List<ItemXmlInfo> items = new ArrayList<>();
}