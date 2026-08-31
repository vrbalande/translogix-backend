package com.translogix.translogix_backend.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    /*
     * 24 hours
     */
    @Value("${jwt.expiration:86400000}")
    private long expiration;

    // =====================================================
    // SIGNING KEY
    // =====================================================

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8));
    }

    // =====================================================
    // GENERATE TOKEN
    // =====================================================

    public String generateToken(
            String username,
            String role) {

        Date now = new Date();

        Date expiryDate = new Date(
                now.getTime() +
                        expiration);

        return Jwts.builder()

                .subject(
                        username)

                .claim(
                        "role",
                        normalizeRole(role))

                .issuedAt(
                        now)

                .expiration(
                        expiryDate)

                .signWith(
                        getSigningKey())

                .compact();
    }

    // =====================================================
    // USERNAME
    // =====================================================

    public String extractUsername(
            String token) {

        return getClaims(
                token).getSubject();
    }

    // =====================================================
    // ROLE
    // =====================================================

    public String extractRole(
            String token) {

        String role = getClaims(token)
                .get(
                        "role",
                        String.class);

        return normalizeRole(
                role);
    }

    // =====================================================
    // TOKEN VALIDATION
    // =====================================================

    public boolean isTokenValid(
            String token) {

        try {

            Claims claims = getClaims(token);

            Date expirationDate = claims.getExpiration();

            return expirationDate != null
                    &&
                    expirationDate.after(
                            new Date());

        } catch (Exception e) {

            System.out.println(
                    "JWT validation failed: "
                            + e.getMessage());

            return false;
        }
    }

    // =====================================================
    // CLAIMS
    // =====================================================

    private Claims getClaims(
            String token) {

        return Jwts.parser()

                .verifyWith(
                        getSigningKey())

                .build()

                .parseSignedClaims(
                        token)

                .getPayload();
    }

    // =====================================================
    // ROLE NORMALIZATION
    // =====================================================

    private String normalizeRole(
            String role) {

        if (role == null ||
                role.trim().isEmpty()) {
            return "";
        }

        role = role.trim()
                .toUpperCase();

        if (role.startsWith(
                "ROLE_")) {

            role = role.substring(5);
        }

        return role;
    }
}