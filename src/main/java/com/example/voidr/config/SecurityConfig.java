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
                .anyRequest().permitAll() // 全てのURLにアクセス許可
            )
            .csrf(csrf -> csrf.disable()) // POSTなどが止まるのを防ぐ
            .formLogin(form -> form.disable()) // ログイン画面を無効化
            .httpBasic(basic -> basic.disable()); // Basic認証を無効化

        return http.build();
    }
}

