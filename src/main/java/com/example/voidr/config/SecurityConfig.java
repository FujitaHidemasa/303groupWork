package com.example.voidr.config;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

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
						.requestMatchers
										(
										"/",
										"/login",
										"/signup",
										"/signup/**",
										"/public/**"
										)
						.permitAll()
						.requestMatchers("/voidrshop").permitAll()
						.requestMatchers(HttpMethod.GET,
								// ストア閲覧系（一覧・詳細・検索）はGETで公開
								"/voidrshop/items",
								"/voidrshop/items/**")
						.permitAll()

						// ★カート操作はログイン必須（/voidrshop/cart/**）
						.requestMatchers("/voidrshop/cart/**").authenticated()

						// ★お気に入りトグルPOST 認証必須
						.requestMatchers(HttpMethod.POST,
								"/voidrshop/items/*/favorite").authenticated()
						
						// それ以外は公開（必要に応じてauthenticated()に切替）
						.anyRequest().permitAll())

				// ★フォームログイン
				.formLogin(login -> login
						.loginPage("/login")
						.loginProcessingUrl("/login") // POST /login
						.usernameParameter("usernameInput") // フォームのnameと一致させる
						.passwordParameter("passwordInput") // フォームのnameと一致させる
						// ★成功時ハンドラ。next→SavedRequest→/voidrshop の順
						.successHandler(authenticationSuccessHandler()) // リダイレクト先
						// ★失敗: next を付け直して /login?error&next=... に戻す
						.failureHandler(authenticationFailureHandler())
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
	
	// ★追加：next を最優先、なければ SavedRequest、最後に /voidrshop
	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		SavedRequestAwareAuthenticationSuccessHandler h = new SavedRequestAwareAuthenticationSuccessHandler();
		h.setTargetUrlParameter("next"); // ← ?next=/voidrshop/items/123 に対応
		h.setDefaultTargetUrl("/voidrshop"); // ← フォールバック
		h.setAlwaysUseDefaultTargetUrl(false); // ← SavedRequestがあればそれを使用
		return h;
	}
	
	// ★追加: 失敗時にも next を維持してログイン画面へ戻す
	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return (request, response, ex) -> {
			String next = request.getParameter("next");
			String url = "/login?error";
			if (next != null && !next.isBlank()) {
				url += "&next=" + URLEncoder.encode(next, StandardCharsets.UTF_8);
			}
			response.sendRedirect(url);
		};
	}

	// Signup後の自動ログインで使用
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception 
	{
		return cfg.getAuthenticationManager();
	}
}
