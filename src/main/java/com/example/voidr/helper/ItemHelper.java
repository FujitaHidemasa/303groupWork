package com.example.voidr.helper;

import java.time.LocalDateTime;

import com.example.voidr.entity.Item;
import com.example.voidr.xml.ItemXmlInfo;

public class ItemHelper
{
	public static Item convertItem(ItemXmlInfo xml) {
	    Item newItem = new Item(
	        xml.getId(),
	        xml.getName(),
	        xml.getPrice(),
	        xml.getOverview(),
	        xml.isDownload(),
	        LocalDateTime.now(),
	        LocalDateTime.now(),
	        xml.getThumbsImageName(),
	        xml.getCategoryList(),
	        xml.getImagesName(),
	        "/images/" + xml.getThumbsImageName() // imagePath
	    );

	    // imagePath をセット
	    newItem.setImagePath("/images/" + xml.getThumbsImageName());

	    return newItem;
	}

}
