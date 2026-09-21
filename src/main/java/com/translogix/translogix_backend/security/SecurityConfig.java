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

        // =========================================================
        // PASSWORD ENCODER
        // =========================================================

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // =========================================================
        // AUTHENTICATION MANAGER
        // =========================================================

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration)
                        throws Exception {

                return configuration.getAuthenticationManager();
        }

        // =========================================================
        // CORS CONFIGURATION
        // =========================================================

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOriginPatterns(
                                List.of(
                                                "http://localhost:*",
                                                "http://127.0.0.1:*",
                                                "https://localhost",
                                                "http://localhost",
                                                "capacitor://localhost",
                                                "ionic://localhost"));

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
                                                "Accept",
                                                "Origin",
                                                "X-Requested-With"));

                configuration.setExposedHeaders(
                                List.of("Authorization"));

                configuration.setAllowCredentials(false);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                                "/**",
                                configuration);

                return source;
        }

        // =========================================================
        // SECURITY FILTER CHAIN
        // =========================================================

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http)
                        throws Exception {

                http

                                // -------------------------------------------------
                                // CSRF
                                // -------------------------------------------------

                                .csrf(csrf -> csrf.disable())

                                // -------------------------------------------------
                                // CORS
                                // -------------------------------------------------

                                .cors(cors -> cors.configurationSource(
                                                corsConfigurationSource()))

                                // -------------------------------------------------
                                // SESSION
                                // -------------------------------------------------

                                .sessionManagement(
                                                session -> session.sessionCreationPolicy(
                                                                SessionCreationPolicy.STATELESS))

                                // -------------------------------------------------
                                // AUTHORIZATION
                                // -------------------------------------------------

                                .authorizeHttpRequests(
                                                auth -> auth

                                                                // CORS preflight
                                                                .requestMatchers(
                                                                                HttpMethod.OPTIONS,
                                                                                "/**")
                                                                .permitAll()

                                                                // =================================
                                                                // AUTH APIs
                                                                // =================================

                                                                .requestMatchers(
                                                                                HttpMethod.POST,
                                                                                "/api/auth/login",
                                                                                "/api/auth/register")
                                                                .permitAll()

                                                                .requestMatchers(
                                                                                "/api/auth/**")
                                                                .permitAll()

                                                                // =================================
                                                                // HEALTH
                                                                // =================================

                                                                .requestMatchers(
                                                                                "/api/health")
                                                                .permitAll()

                                                                // =================================
                                                                // PUBLIC SHIPMENTS
                                                                // =================================

                                                                .requestMatchers(
                                                                                HttpMethod.GET,
                                                                                "/api/shipments",
                                                                                "/api/shipments/**")
                                                                .permitAll()

                                                                // =================================
                                                                // ADMIN
                                                                // =================================

                                                                .requestMatchers(
                                                                                "/api/admin/**")
                                                                .hasRole("ADMIN")

                                                                // =================================
                                                                // USER
                                                                // =================================

                                                                .requestMatchers(
                                                                                "/api/user/**")
                                                                .hasRole("USER")

                                                                // =================================
                                                                // SHIPMENT WRITE APIs
                                                                // =================================

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

                                                                // =================================
                                                                // EVERYTHING ELSE
                                                                // =================================

                                                                .anyRequest()
                                                                .authenticated())

                                // -------------------------------------------------
                                // JWT FILTER
                                // -------------------------------------------------

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}