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

/**
 * XMLの1つの商品を表すクラス。
 * <p>
 * XML例:
 * <pre>
 * &lt;item id="1"&gt;
 *     &lt;name&gt;商品名&lt;/name&gt;
 *     &lt;thumbs&gt;サムネイル画像名&lt;/thumbs&gt;
 *     &lt;isDownload&gt;true/false&lt;/isDownload&gt;
 *     &lt;price&gt;価格&lt;/price&gt;
 *     &lt;overview&gt;商品概要&lt;/overview&gt;
 *     &lt;categories&gt;
 *         &lt;category&gt;カテゴリ名&lt;/category&gt;
 *     &lt;/categories&gt;
 *     &lt;images&gt;
 *         &lt;image&gt;画像名&lt;/image&gt;
 *     &lt;/images&gt;
 * &lt;/item&gt;
 * </pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemXmlInfo
{

	/**
	 * 商品ID。
	 * XML属性 "id" に対応。
	 */
	@XmlAttribute(name = "id")
	private long id;

	/**
	 * 商品名。
	 * XML要素 "name" に対応。
	 */
	@XmlElement(name = "name")
	private String name;

	/**
	 * 商品の価格（円）。
	 * XML要素 "price" に対応。
	 */
	@XmlElement(name = "price")
	private int price;

	/**
	 * 商品概要。
	 * XML要素 "overview" に対応。
	 */
	@XmlElement(name = "overview")
	private String overview;

	/**
	 * ダウンロード専用商品かどうか。
	 * XML要素 "isDownload" に対応。
	 */
	@XmlElement(name = "isDownload")
	private boolean isDownload;

	/**
	 * サムネイル画像のファイル名。
	 * XML要素 "thumbs" に対応。
	 */
	@XmlElement(name = "thumbs")
	private String thumbsImageName;

	/**
	 * 商品カテゴリのリスト。
	 * XML要素 "categories" 内の複数 "category" に対応。
	 */
	@XmlElementWrapper(name = "categories")
	@XmlElement(name = "category")
	private List<String> categoryList = new ArrayList<>();

	/**
	 * 商品画像のリスト。
	 * XML要素 "images" 内の複数 "image" に対応。
	 */
	@XmlElementWrapper(name = "images")
	@XmlElement(name = "image")
	private List<String> imagesName = new ArrayList<>();
}
