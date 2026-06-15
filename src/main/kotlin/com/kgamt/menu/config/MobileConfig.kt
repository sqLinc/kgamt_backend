package com.kgamt.menu.config

import com.kgamt.menu.service.JwtService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@Order(1)
class MobileConfig {

    @Bean
    fun apiSecurityFilterChain(
        http: HttpSecurity,
        jwtService: JwtService
    ): SecurityFilterChain {

        http
            .securityMatcher("/leader/**", "/api/**", "/test/**")

            .csrf { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }

            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            .authorizeHttpRequests {
                it
                    .requestMatchers("/leader/login").permitAll()
                    .requestMatchers("/leader/send_request").authenticated()
                    .requestMatchers("/leader/get_today_request").authenticated()
                    .requestMatchers("/leader/request/update").authenticated()
                    .requestMatchers("/leader/request/all").authenticated()
                    .requestMatchers("/api/**").permitAll()
                    .requestMatchers("/test/**").authenticated()
            }


            .exceptionHandling {
                it.authenticationEntryPoint { _, response, _ ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                }
            }

            .addFilterBefore(
                JwtFilter(jwtService),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }
}