package com.kgamt.menu.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.filter.HiddenHttpMethodFilter


@Configuration
@EnableWebSecurity
@Order(2)
class WebConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/login").permitAll()
                    .requestMatchers("/leader/**").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/menu").permitAll()
                    .requestMatchers("/api/**").permitAll()
                    .anyRequest().authenticated()

            }
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .defaultSuccessUrl("/main-screen", true)
                    .permitAll()
            }
            .logout { logout ->
                logout.logoutSuccessUrl("/login")
            }
        return http.build()
    }

    @Bean
    fun passwordEncode(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun userDetailService(passwordEncoder: PasswordEncoder): UserDetailsService {
        val admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin"))
            .roles("ADMIN")
            .build()
        val leader = User.builder()
            .username("leader")
            .password(passwordEncoder.encode("leader"))
            .roles("LEADER")
            .build()
        return InMemoryUserDetailsManager(admin, leader)
    }
    @Bean
    fun hiddenHttpMethodFilter(): HiddenHttpMethodFilter {
        return HiddenHttpMethodFilter()
    }


}