package com.NBE3_4_2_Team4.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

@Configuration
public class SecurityConfig {

    private List<RequestMatcher> needAuthenticated(String pattern){

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> {
                        req.requestMatchers("/admin/**").hasRole("ADMIN");
                        needAuthenticated(req, "/questions/**");
                        needAuthenticated(req, "/answers/**");
                        needAuthenticated(req, "/products/**");
                        });
        return http.build();
    }

    private void needAuthenticated(AuthorizeHttpRequestsConfigurer<?>.AuthorizationManagerRequestMatcherRegistry req, String pattern){
        req.requestMatchers(HttpMethod.POST,  pattern).authenticated();
        req.requestMatchers(HttpMethod.PUT,  pattern).authenticated();
        req.requestMatchers(HttpMethod.PATCH,  pattern).authenticated();
        req.requestMatchers(HttpMethod.DELETE,  pattern).authenticated();
    }
}
