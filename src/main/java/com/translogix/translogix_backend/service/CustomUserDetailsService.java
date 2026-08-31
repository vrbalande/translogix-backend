package com.translogix.translogix_backend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.translogix.translogix_backend.entity.User;
import com.translogix.translogix_backend.repository.UserRepository;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                "User not found: " + username));

        String role = user.getRole();

        if (role == null ||
                role.trim().isEmpty()) {

            throw new UsernameNotFoundException(
                    "User role not found for: " +
                            username);
        }

        role = role.trim()
                .toUpperCase();

        // Database contains ROLE_ADMIN
        // or ROLE_USER -> normalize it

        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(
                        user.getUsername())
                .password(
                        user.getPassword())
                .roles(
                        role)
                .build();
    }
}