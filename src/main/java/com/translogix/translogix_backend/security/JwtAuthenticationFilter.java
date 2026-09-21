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

        // Public endpoints - no JWT required
        if (requestUri.startsWith("/api/auth/")
                || requestUri.equals("/api/health")) {

            filterChain.doFilter(request, response);
            return;
        }

        String authHeader =
                request.getHeader("Authorization");

        // No Authorization header
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authHeader.substring(7).trim();

        // Empty token
        if (token.isEmpty()) {

            filterChain.doFilter(request, response);
            return;
        }

        try {

            // Validate JWT
            if (!jwtService.isTokenValid(token)) {

                System.out.println("JWT TOKEN INVALID");

                filterChain.doFilter(request, response);
                return;
            }

            // Extract username
            String username =
                    jwtService.extractUsername(token);

            if (username == null
                    || username.trim().isEmpty()) {

                System.out.println(
                        "JWT USERNAME MISSING");

                filterChain.doFilter(request, response);
                return;
            }

            // Set authentication
            if (SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

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

            // CORRECT METHOD
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
