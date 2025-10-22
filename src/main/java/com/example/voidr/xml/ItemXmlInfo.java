package com.example.voidr.xml;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemXmlInfo {

    @XmlAttribute(name = "id")
    private long id;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "price")
    private int price;

    @XmlElement(name = "overview")
    private String overview;

    @XmlElementWrapper(name = "categories")
    @XmlElement(name = "category")
    private List<String> categoryList = new ArrayList<>();;
}