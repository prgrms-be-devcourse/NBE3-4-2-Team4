package com.NBE3_4_2_Team4.global.config;

import com.NBE3_4_2_Team4.global.security.accessDeniedHandler.CustomAccessDeniedHandler;
import com.NBE3_4_2_Team4.global.security.authenticationEntryPoint.CustomAuthenticationEntryPoint;
import com.NBE3_4_2_Team4.global.security.filter.CustomJwtFilter;
import com.NBE3_4_2_Team4.global.security.oauth2.CustomOAuth2AccessTokenResponseClient;
import com.NBE3_4_2_Team4.global.security.oauth2.CustomOAuth2RequestResolver;
import com.NBE3_4_2_Team4.global.security.oauth2.CustomOAuth2SuccessHandler;
import com.NBE3_4_2_Team4.global.security.user.customUser.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
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
                    needAuthenticated(req, "/api/messages/**");

                    needEmailVerified(req, "/api/test");
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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        return http.build();
    }

    private void needAuthenticated(AuthorizeHttpRequestsConfigurer<?>.AuthorizationManagerRequestMatcherRegistry req, String pattern){
        req.requestMatchers(HttpMethod.POST,  pattern).authenticated();
        req.requestMatchers(HttpMethod.PUT,  pattern).authenticated();
        req.requestMatchers(HttpMethod.PATCH,  pattern).authenticated();
        req.requestMatchers(HttpMethod.DELETE,  pattern).authenticated();
    }

    private boolean isEmailVerified(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUser) {
            return ((CustomUser) principal).getMember().isEmailVerified();
        }

        return false;
    }

    private AuthorizationDecision authDecisionByEmailVerified(Authentication auth) {
        return isEmailVerified(auth) ?
                new AuthorizationDecision(true) :
                new AuthorizationDecision(false);
    }

    private void needEmailVerified(AuthorizeHttpRequestsConfigurer<?>.AuthorizationManagerRequestMatcherRegistry req,
                                   String pattern) {
        req.requestMatchers(HttpMethod.POST, pattern).access((auth, context) ->
                authDecisionByEmailVerified(auth.get())
        );
        req.requestMatchers(HttpMethod.PUT, pattern).access((auth, context) ->
                authDecisionByEmailVerified(auth.get())
        );
        req.requestMatchers(HttpMethod.PATCH, pattern).access((auth, context) ->
                authDecisionByEmailVerified(auth.get())
        );
        req.requestMatchers(HttpMethod.DELETE, pattern).access((auth, context) ->
                authDecisionByEmailVerified(auth.get())
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration emailVerifyingConfiguration = new CorsConfiguration();
        emailVerifyingConfiguration.addAllowedOrigin("*");
        emailVerifyingConfiguration.addAllowedMethod("POST");
        emailVerifyingConfiguration.addAllowedHeader("*");
        source.registerCorsConfiguration("/api/members/verify-email", emailVerifyingConfiguration);

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
