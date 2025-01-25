package com.NBE3_4_2_Team4.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> {
                        req.requestMatchers("/admin/**").hasRole("ADMIN");
                        needAuthenticated(req, "/api/questions/**");
                        needAuthenticated(req, "/api/answers/**");
                        needAuthenticated(req, "/api/products/**");
                        req.anyRequest().permitAll();
                        })
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)); //h2-console 정상 작동용
        return http.build();
    }

    private void needAuthenticated(AuthorizeHttpRequestsConfigurer<?>.AuthorizationManagerRequestMatcherRegistry req, String pattern){
        req.requestMatchers(HttpMethod.POST,  pattern).authenticated();
        req.requestMatchers(HttpMethod.PUT,  pattern).authenticated();
        req.requestMatchers(HttpMethod.PATCH,  pattern).authenticated();
        req.requestMatchers(HttpMethod.DELETE,  pattern).authenticated();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
