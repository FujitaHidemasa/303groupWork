package com.example.voidr.xml;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.example.voidr.entity.Item;

import lombok.Data;

@Data
@XmlRootElement(name = "itemList")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemListXml
{
	@XmlElement(name = "item")
	private List<Item> items;
}
