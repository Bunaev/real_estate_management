package com.search_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * TODO: Перед развёртыванием в production необходимо:
     * 1. Включить CSRF-защиту
     * 2. Настроить аутентификацию (OAuth2/JWT)
     * 3. Настроить ролевую модель доступа к эндпоинтам
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl) auth.anyRequest()).permitAll());
        return http.build();
    }
}