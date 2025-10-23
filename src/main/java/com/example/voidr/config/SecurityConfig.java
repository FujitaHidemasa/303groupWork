package com.example.voidr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.voidr.service.impl.CustomUserDetailsService;

@Configuration
public class SecurityConfig
{
	@Bean
	public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService  userDetailsService,
			PasswordEncoder passwordEncoder)
	{
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder);
		return authProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		http
	    .authorizeHttpRequests(auth -> auth
	        .requestMatchers("/login", "/public/**").permitAll()
	        .anyRequest().permitAll()  // 全てのページを未ログインでも閲覧可能に
	    )
	    .formLogin(form -> form
	        .loginPage("/login")
	        .permitAll()
	        .defaultSuccessUrl("/", true) // ログイン成功後のリダイレクト先
	    );


		return http.build();
	}
}