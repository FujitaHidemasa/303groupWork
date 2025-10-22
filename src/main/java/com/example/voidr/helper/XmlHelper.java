package com.example.voidr.helper;

import java.io.File;
import java.io.IOException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * {@code XmlHelper} クラスは、XMLファイルを指定したクラス型にデシリアライズ
 * （Javaオブジェクトに変換）するためのユーティリティクラスです。
 * <p>
 * 内部的には JAXB (Java Architecture for XML Binding) を使用しています。
 * </p>
 *
 * <p>使用例:</p>
 * <pre>{@code
 * try {
 *     ItemXml info = XmlHelper.loadXmlClass("data/CharacterInfo.xml", ItemXml.class);
 *     System.out.println(info.getName());
 * } catch (IOException | JAXBException e) {
 *     e.printStackTrace();
 * }
 * }</pre>
 *
 * @author 林 佳樹
 * @version 1.0
 */
public class XmlHelper {
	/**
	 * 指定された {@link File} オブジェクトからXMLを読み込み、
	 * 対応するクラス型のオブジェクトに変換します。
	 *
	 * @param <T>    デシリアライズ先のクラス型
	 * @param file   読み込むXMLファイル
	 * @param clazz  変換先のクラス
	 * @return       XMLを変換した {@code clazz} 型のインスタンス
	 * @throws IOException   ファイルが存在しない、またはアクセスできない場合
	 * @throws JAXBException XMLの解析またはマッピングに失敗した場合
	 */
	public static <T> T loadXmlClass(File file, Class<T> clazz) throws IOException, JAXBException {
		// ファイルの存在確認
		if (file == null || !file.exists()) {
			throw new IOException("指定されたXMLファイルが存在しません: " +
					(file != null ? file.getAbsolutePath() : "null"));
		}

		// JAXBコンテキストの生成
		JAXBContext context = JAXBContext.newInstance(clazz);

		// Unmarshallerを作成し、XMLをJavaオブジェクトに変換
		Unmarshaller unmarshaller = context.createUnmarshaller();

		Object result = unmarshaller.unmarshal(file);

		// 型チェックを行って安全にキャスト
		if (!clazz.isInstance(result)) {
			throw new JAXBException("XMLを指定クラス " + clazz.getName() + " に変換できません。");
		}

		return clazz.cast(result);
	}

	/**
	 * 指定されたファイルパスからXMLを読み込み、
	 * 対応するクラス型のオブジェクトに変換します。
	 * <p>
	 * 内部的には {@link #loadXmlClass(File, Class)} を呼び出します。
	 * </p>
	 *
	 * @param <T>    デシリアライズ先のクラス型
	 * @param path   読み込むXMLファイルのパス
	 * @param clazz  変換先のクラス
	 * @return       XMLを変換した {@code clazz} 型のインスタンス
	 * @throws IOException   ファイルが存在しない、またはアクセスできない場合
	 * @throws JAXBException XMLの解析またはマッピングに失敗した場合
	 */
	public static <T> T loadXmlClass(String path, Class<T> clazz) throws IOException, JAXBException {
		if (path == null || path.isEmpty()) {
			throw new IOException("ファイルパスが指定されていません。");
		}

		File file = new File(path);
		return loadXmlClass(file, clazz);
	}
}
