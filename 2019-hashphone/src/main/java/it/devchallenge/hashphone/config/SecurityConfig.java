package it.devchallenge.hashphone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf().disable()
            .httpBasic()
            .and()
            .authorizeExchange()
            .anyExchange()
            .authenticated()
            .and()
            .build();
    }
}
