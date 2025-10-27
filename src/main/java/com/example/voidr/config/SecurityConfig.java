package com.example.voidr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 公開ページは未ログインでもアクセス可能
                .requestMatchers("/login", "/public/**", "/voidrshop", "/voidrshop/items").permitAll()
                // 認証が必要なページだけ指定
                .requestMatchers("/voidrshop/cart/**").authenticated()
                .anyRequest().permitAll() // それ以外は未ログインでも閲覧可能
            )
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("usernameInput")
                .passwordParameter("passwordInput")
                .defaultSuccessUrl("/voidrshop", false) // 元のページに戻る
                .failureUrl("/login?error")
            );

        return http.build();
    }
}

