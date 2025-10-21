package com.example.voidr.helper;

import java.io.File;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

public class XmlHelper
{

	public static <T> T loadXml(File path, Class<T> clazz) throws Exception
	{
		JAXBContext context = JAXBContext.newInstance(clazz);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return clazz.cast(unmarshaller.unmarshal(path));
	}

	public static <T> T loadXml(String path, Class<T> clazz) throws Exception
	{
		JAXBContext context = JAXBContext.newInstance(clazz);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return clazz.cast(unmarshaller.unmarshal(new File(path)));
	}
}
