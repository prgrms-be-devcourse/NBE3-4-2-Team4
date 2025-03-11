package com.NBE3_4_2_Team4.global.config

import com.NBE3_4_2_Team4.global.security.accessDeniedHandler.CustomAccessDeniedHandler
import com.NBE3_4_2_Team4.global.security.authenticationEntryPoint.CustomAuthenticationEntryPoint
import com.NBE3_4_2_Team4.global.security.filter.CustomJwtFilter
import com.NBE3_4_2_Team4.global.security.oauth2.CustomOAuth2AccessTokenResponseClient
import com.NBE3_4_2_Team4.global.security.oauth2.CustomOAuth2RequestResolver
import com.NBE3_4_2_Team4.global.security.oauth2.CustomOAuth2SuccessHandler
import com.NBE3_4_2_Team4.global.security.user.customUser.CustomUser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@EnableWebSecurity
@Configuration
class SecurityConfig(
    val customJwtFilter: CustomJwtFilter,
    val oAuth2RequestResolver: CustomOAuth2RequestResolver,
    val oAuth2SuccessHandler: CustomOAuth2SuccessHandler,
    val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    val customAccessDeniedHandler: CustomAccessDeniedHandler,
    val accessTokenResponseClient: CustomOAuth2AccessTokenResponseClient
) {
    @Bean
    @Throws(Exception::class)
    fun SecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .invoke {
                cors {
                    configurationSource = corsConfigurationSource()
                }
                formLogin { disable() }
                csrf {disable()}
                authorizeHttpRequests {
                    authorize(HttpMethod.POST, "/api/logout", authenticated)
                    authorize(HttpMethod.POST, "/api/login", permitAll)
                    authorize(HttpMethod.POST, "/api/admin/login", permitAll)
                    authorize("/api/admin/**", hasRole("ADMIN"))

                    authorize(HttpMethod.GET, "/api/questions/**", permitAll)
                    authorize("/api/questions/**", authenticated)

                    authorize(HttpMethod.GET, "/api/answers/**", permitAll)
                    authorize("/api/answers/**", authenticated)

                    authorize(HttpMethod.GET, "/api/products/**", permitAll)
                    authorize("/api/products/**", authenticated)

                    authorize(HttpMethod.GET, "/api/points/**", permitAll)
                    authorize("/api/points/**", authenticated)

                    authorize(HttpMethod.GET, "/api/test", permitAll)
                    authorize("/api/test", needEmailVerified())
                    authorize(anyRequest, permitAll)
                }
                sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
                addFilterBefore<UsernamePasswordAuthenticationFilter>(customJwtFilter)
                headers {
                    frameOptions {
                        disable()
                    }
                }
                exceptionHandling {
                    authenticationEntryPoint = customAuthenticationEntryPoint
                    accessDeniedHandler = customAccessDeniedHandler
                }
                oauth2Login {
                    oAuth2SuccessHandler
                    oAuth2RequestResolver
                    accessTokenResponseClient
                }
            }
            return http.build()
    }


    private fun isEmailVerified(auth: Authentication?): Boolean {
        if (auth == null || !auth.isAuthenticated) {
            return false
        }

        val principal = auth.principal

        if (principal is CustomUser) {
            return principal.member.emailVerified
        }

        return false
    }

    private fun needEmailVerified(): AuthorizationManager<RequestAuthorizationContext> {
        return AuthorizationManager { authentication, _ ->
            AuthorizationDecision(isEmailVerified(authentication.get()))
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()

        val emailVerifyingConfiguration = CorsConfiguration()
        emailVerifyingConfiguration.addAllowedOrigin("*")
        emailVerifyingConfiguration.addAllowedMethod("POST")
        emailVerifyingConfiguration.addAllowedHeader("*")
        source.registerCorsConfiguration("/api/members/verify-email", emailVerifyingConfiguration)

        val configuration = CorsConfiguration()
        configuration.addAllowedOrigin("http://localhost:3000")
        configuration.addAllowedMethod("*")
        configuration.addAllowedHeader("*")
        configuration.allowCredentials = true
        source.registerCorsConfiguration("/**", configuration)

        return source
    }
}