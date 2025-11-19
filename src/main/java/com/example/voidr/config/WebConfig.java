package com.example.voidr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 画像配信設定：
 * /images/** で
 *   - src/main/resources/static/images/
 *   - プロジェクト直下 ./uploads/item-thumbs/
 * の両方を見る
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry.addResourceHandler("/images/**")
				.addResourceLocations(
						"classpath:/static/images/", // 既存の静的画像
						"file:./uploads/item-thumbs/" // アップロードされたサムネイル
				);
	}
}
