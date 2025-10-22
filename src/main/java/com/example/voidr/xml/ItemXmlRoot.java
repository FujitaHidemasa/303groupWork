package com.example.voidr.xml;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "itemList")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemXmlRoot
{
	@XmlElement(name = "item")
	private List<ItemXmlInfo> items = new ArrayList<>();
}
