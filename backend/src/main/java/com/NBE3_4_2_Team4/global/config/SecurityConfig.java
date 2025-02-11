package com.NBE3_4_2_Team4.global.config;

import com.NBE3_4_2_Team4.global.security.accessDeniedHandler.CustomAccessDeniedHandler;
import com.NBE3_4_2_Team4.global.security.authenticationEntryPoint.CustomAuthenticationEntryPoint;
import com.NBE3_4_2_Team4.global.security.filter.CustomJwtFilter;
import com.NBE3_4_2_Team4.global.security.oauth2.CustomOAuth2AccessTokenResponseClient;
import com.NBE3_4_2_Team4.global.security.oauth2.CustomOAuth2RequestResolver;
import com.NBE3_4_2_Team4.global.security.oauth2.CustomOAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig{
    private final CustomJwtFilter customJwtFilter;
    private final CustomOAuth2RequestResolver oAuth2RequestResolver;
    private final CustomOAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomOAuth2AccessTokenResponseClient accessTokenResponseClient;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(HttpMethod.POST, "/api/logout").authenticated();
                    req.requestMatchers(HttpMethod.POST, "/api/admin/login").permitAll();
                    req.requestMatchers("/api/admin/**").hasRole("ADMIN");
                    needAuthenticated(req, "/api/questions/**");
                    needAuthenticated(req, "/api/answers/**");
                    needAuthenticated(req, "/api/products/**");
                    needAuthenticated(req, "/api/points/**");
                    req.anyRequest().permitAll();
                })
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))//h2-console 정상 작동용
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(authenticationEntryPoint);
                    exception.accessDeniedHandler(accessDeniedHandler);
                })
                .oauth2Login(
                        oauth2Login             ->
                        {
                            oauth2Login.successHandler(oAuth2SuccessHandler);
                            oauth2Login.authorizationEndpoint(authorizationEndpointConfig ->
                                    authorizationEndpointConfig.authorizationRequestResolver(oAuth2RequestResolver));
                            oauth2Login.tokenEndpoint(tokenEndpointConfig ->
                                    tokenEndpointConfig.accessTokenResponseClient(accessTokenResponseClient));
                        })
                .cors(Customizer.withDefaults());
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");  // 허용할 origin
        configuration.addAllowedMethod("*");  // 허용할 HTTP 메서드
        configuration.addAllowedHeader("*");  // 허용할 HTTP 헤더
        configuration.setAllowCredentials(true);  // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 모든 경로에 대해 CORS 설정

        return source;
    }
}
