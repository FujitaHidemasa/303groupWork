package com.example.voidr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // ★追加：@PreAuthorize が有効になる
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception 
	{
		http
				.authorizeHttpRequests(auth -> auth
						// ★静的リソースは常に許可
						.requestMatchers("/css/**", "/js/**", "/images/**", "/image/**", "/webjars/**").permitAll()

						// ★公開ページ（未ログインでもOK）
						.requestMatchers("/", "/login", "/signup", "/public/**").permitAll()
						.requestMatchers("/voidrshop").permitAll()
						.requestMatchers(HttpMethod.GET,
								// ストア閲覧系（一覧・詳細・検索）はGETで公開
								"/voidrshop/items",
								"/voidrshop/items/**")
						.permitAll()

						// ★カート操作はログイン必須（/voidrshop/cart/**）
						.requestMatchers("/voidrshop/cart/**").authenticated()

						// それ以外は公開（必要に応じてauthenticated()に切替）
						.anyRequest().permitAll())

				// ★フォームログイン
				.formLogin(login -> login
						.loginPage("/login")
						.loginProcessingUrl("/login") // POST /login
						.usernameParameter("usernameInput") // フォームのnameと一致させる
						.passwordParameter("passwordInput") // フォームのnameと一致させる
						.defaultSuccessUrl("/voidrshop", false) // リダイレクト先
						.failureUrl("/login?error")
						.permitAll())

				// ★ログアウト（任意だが入れておくと便利）
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/voidrshop")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.permitAll())

		// CSRFはデフォルト有効（POSTフォームにトークンを入れてあるのでOK）
		// .csrf(csrf -> { /* デフォルトのまま */ })
		;

		return http.build();
	}

	// Signup後の自動ログインで使用
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception 
	{
		return cfg.getAuthenticationManager();
	}
}
