package com.AnibalValter.todoSimple.configs;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.AnibalValter.todoSimple.security.JWTUtil;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTUtil jwtUtil;

    // Padrões de URLs públicas acessíveis sem autenticação
    private static final String[] PUBLIC_MATCHERS = {
            "/"
    };

    // Padrões de URLs públicas acessíveis sem autenticação, permitindo apenas
    // requisições POST
    private static final String[] PUBLIC_MATCHERS_POST = {
            "/user",
            "/login"
    };

    // Configuração do filtro de segurança HTTP
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Desabilita a proteção CSRF e habilita a configuração de CORS
        http.cors().and().csrf().disable();

        AuthenticationManagerBuilder authenticationManagerBuilder = http
        .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(this.userDetailsService)
        .passwordEncoder(bCryptPasswordEncoder());

        this.authenticationManager = authenticationManagerBuilder.build();

        // Configuração de autorização das requisições
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST).permitAll()
                .antMatchers(PUBLIC_MATCHERS).permitAll()
                .anyRequest().authenticated();

        // Configuração para uso de sessões sem estado (STATELESS)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Retorna a configuração de segurança
        return http.build();
    }

    // Configuração de CORS para permitir requisições de diferentes origens
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Bean para fornecer um codificador de senha BCrypt
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
