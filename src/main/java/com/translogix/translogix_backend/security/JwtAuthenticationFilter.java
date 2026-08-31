
package com.translogix.translogix_backend.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtService jwtService;
        private final UserDetailsService userDetailsService;

        public JwtAuthenticationFilter(
                        JwtService jwtService,
                        UserDetailsService userDetailsService) {

                this.jwtService = jwtService;
                this.userDetailsService = userDetailsService;
        }

        @Override
        protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {

                String requestUri = request.getRequestURI();

                // =================================================
                // PUBLIC AUTH ENDPOINTS
                // =================================================

                if (requestUri.startsWith("/api/auth/")
                                || requestUri.equals("/api/health")) {

                        filterChain.doFilter(
                                        request,
                                        response);

                        return;
                }

                // =================================================
                // READ AUTHORIZATION HEADER
                // =================================================

                String authHeader = request.getHeader("Authorization");

                if (authHeader == null
                                || !authHeader.startsWith("Bearer ")) {

                        filterChain.doFilter(
                                        request,
                                        response);

                        return;
                }

                String token = authHeader.substring(7).trim();

                if (token.isEmpty()) {

                        filterChain.doFilter(
                                        request,
                                        response);

                        return;
                }

                try {

                        // =================================================
                        // VALIDATE TOKEN
                        // =================================================

                        if (!jwtService.isTokenValid(token)) {

                                System.out.println(
                                                "JWT TOKEN INVALID");

                                filterChain.doFilter(
                                                request,
                                                response);

                                return;
                        }

                        // =================================================
                        // GET USERNAME
                        // =================================================

                        String username = jwtService.extractUsername(token);

                        if (username == null
                                        || username.trim().isEmpty()) {

                                System.out.println(
                                                "JWT USERNAME MISSING");

                                filterChain.doFilter(
                                                request,
                                                response);

                                return;
                        }

                        // =================================================
                        // ONLY SET AUTHENTICATION IF NOT ALREADY SET
                        // =================================================

                        if (SecurityContextHolder
                                        .getContext()
                                        .getAuthentication() == null) {

                                // ---------------------------------------------
                                // LOAD USER FROM DATABASE
                                // ---------------------------------------------

                                UserDetails userDetails = userDetailsService
                                                .loadUserByUsername(
                                                                username);

                                // ---------------------------------------------
                                // IMPORTANT:
                                // Use authorities from UserDetails
                                // ---------------------------------------------

                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                                userDetails,
                                                null,
                                                userDetails.getAuthorities());

                                authentication.setDetails(
                                                new WebAuthenticationDetailsSource()
                                                                .buildDetails(request));

                                SecurityContextHolder
                                                .getContext()
                                                .setAuthentication(
                                                                authentication);

                                System.out.println(
                                                "JWT AUTHENTICATION SUCCESS");

                                System.out.println(
                                                "USERNAME = "
                                                                + userDetails.getUsername());

                                System.out.println(
                                                "AUTHORITIES = "
                                                                + userDetails.getAuthorities());
                        }

                } catch (Exception e) {

                        System.out.println(
                                        "JWT AUTHENTICATION FAILED: "
                                                        + e.getMessage());

                        SecurityContextHolder
                                        .clearContext();
                }

                // =================================================
                // CONTINUE REQUEST
                // =================================================

                filterChain.doFilter(
                                request,
                                response);
        }
}
