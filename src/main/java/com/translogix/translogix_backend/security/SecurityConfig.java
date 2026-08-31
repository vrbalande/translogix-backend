package com.translogix.translogix_backend.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter) {

                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        // =====================================================
        // PASSWORD ENCODER
        // =====================================================

        @Bean
        public PasswordEncoder passwordEncoder() {

                return new BCryptPasswordEncoder();
        }

        // =====================================================
        // AUTHENTICATION MANAGER
        // =====================================================

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration) throws Exception {

                return configuration
                                .getAuthenticationManager();
        }

        // =====================================================
        // CORS
        // =====================================================

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(
                                List.of(
                                                "http://127.0.0.1:5500",
                                                "http://localhost:5500"));

                configuration.setAllowedMethods(
                                List.of(
                                                "GET",
                                                "POST",
                                                "PUT",
                                                "PATCH",
                                                "DELETE",
                                                "OPTIONS"));

                configuration.setAllowedHeaders(
                                List.of(
                                                "Authorization",
                                                "Content-Type",
                                                "Accept"));

                configuration.setExposedHeaders(
                                List.of(
                                                "Authorization"));

                configuration.setAllowCredentials(false);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                                "/**",
                                configuration);

                return source;
        }

        // =====================================================
        // SECURITY FILTER CHAIN
        // =====================================================

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http

                                // =================================================
                                // CSRF
                                // =================================================

                                .csrf(
                                                csrf -> csrf.disable())

                                // =================================================
                                // CORS
                                // =================================================

                                .cors(
                                                cors -> cors.configurationSource(
                                                                corsConfigurationSource()))

                                // =================================================
                                // JWT = STATELESS
                                // =================================================

                                .sessionManagement(
                                                session -> session.sessionCreationPolicy(
                                                                SessionCreationPolicy.STATELESS))

                                // =================================================
                                // AUTHORIZATION
                                // =================================================

                                .authorizeHttpRequests(
                                                auth -> auth

                                                                // ---------------------------------
                                                                // OPTIONS / CORS PREFLIGHT
                                                                // ---------------------------------

                                                                .requestMatchers(
                                                                                HttpMethod.OPTIONS,
                                                                                "/**")
                                                                .permitAll()

                                                                // ---------------------------------
                                                                // PUBLIC AUTHENTICATION
                                                                // ---------------------------------

                                                                .requestMatchers(
                                                                                "/api/auth/**",
                                                                                "/api/health")
                                                                .permitAll()

                                                                // ---------------------------------
                                                                // PUBLIC SHIPMENT GET
                                                                // ---------------------------------
                                                                //
                                                                // Development / testing:
                                                                // Browser can open:
                                                                //
                                                                // http://localhost:8081/api/shipments
                                                                //
                                                                // ---------------------------------

                                                                .requestMatchers(
                                                                                HttpMethod.GET,
                                                                                "/api/shipments",
                                                                                "/api/shipments/**")
                                                                .permitAll()

                                                                // ---------------------------------
                                                                // ADMIN APIs
                                                                // ---------------------------------

                                                                .requestMatchers(
                                                                                "/api/admin/**")
                                                                .hasRole("ADMIN")

                                                                // ---------------------------------
                                                                // USER APIs
                                                                // ---------------------------------

                                                                .requestMatchers(
                                                                                "/api/user/**")
                                                                .hasRole("USER")

                                                                // ---------------------------------
                                                                // SHIPMENT WRITE OPERATIONS
                                                                // ---------------------------------

                                                                .requestMatchers(
                                                                                HttpMethod.POST,
                                                                                "/api/shipments/**")
                                                                .authenticated()

                                                                .requestMatchers(
                                                                                HttpMethod.PUT,
                                                                                "/api/shipments/**")
                                                                .authenticated()

                                                                .requestMatchers(
                                                                                HttpMethod.PATCH,
                                                                                "/api/shipments/**")
                                                                .authenticated()

                                                                .requestMatchers(
                                                                                HttpMethod.DELETE,
                                                                                "/api/shipments/**")
                                                                .authenticated()

                                                                // ---------------------------------
                                                                // EVERYTHING ELSE
                                                                // ---------------------------------

                                                                .anyRequest()
                                                                .authenticated())

                                // =================================================
                                // JWT FILTER
                                // =================================================

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}