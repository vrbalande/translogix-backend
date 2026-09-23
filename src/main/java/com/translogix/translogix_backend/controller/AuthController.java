package com.translogix.translogix_backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.translogix.translogix_backend.dto.RegisterRequest;
import com.translogix.translogix_backend.entity.User;
import com.translogix.translogix_backend.repository.UserRepository;
import com.translogix.translogix_backend.security.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        try {

            // -------------------------------------------------
            // USERNAME
            // -------------------------------------------------

            String username = request.getUsername() == null
                    ? ""
                    : request.getUsername().trim();

            // -------------------------------------------------
            // PASSWORD
            // -------------------------------------------------

            String password = request.getPassword() == null
                    ? ""
                    : request.getPassword();

            // -------------------------------------------------
            // VALIDATION
            // -------------------------------------------------

            if (username.isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Username is required."
                                )
                        );
            }

            if (password.isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Password is required."
                                )
                        );
            }

            if (username.length() < 3) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Username must contain at least 3 characters."
                                )
                        );
            }

            if (password.length() < 6) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Password must contain at least 6 characters."
                                )
                        );
            }

            // -------------------------------------------------
            // CHECK DUPLICATE USERNAME
            // -------------------------------------------------

            if (userRepository.existsByUsername(username)) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(
                                Map.of(
                                        "message",
                                        "Username already exists."
                                )
                        );
            }

            // -------------------------------------------------
            // ROLE
            // -------------------------------------------------

            String role = request.getRole();

            if (role == null || role.trim().isEmpty()) {

                role = "USER";

            } else {

                role = role.trim().toUpperCase();

                if (role.startsWith("ROLE_")) {

                    role = role.substring(5);
                }
            }

            // -------------------------------------------------
            // VALIDATE ROLE
            // -------------------------------------------------

            if (!role.equals("USER") &&
                    !role.equals("ADMIN")) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Role must be USER or ADMIN."
                                )
                        );
            }

            // -------------------------------------------------
            // CREATE USER
            // -------------------------------------------------

            User user = new User();

            user.setUsername(username);

            // IMPORTANT:
            // Never save plain-text password
            user.setPassword(
                    passwordEncoder.encode(password)
            );

            user.setRole(role);

            User savedUser =
                    userRepository.save(user);

            // -------------------------------------------------
            // RESPONSE
            // -------------------------------------------------

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "User registered successfully."
            );

            response.put(
                    "username",
                    savedUser.getUsername()
            );

            response.put(
                    "role",
                    savedUser.getRole()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "message",
                                    "Registration failed.",
                                    "error",
                                    e.getMessage() == null
                                            ? "Unknown error"
                                            : e.getMessage()
                            )
                    );
        }
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> loginRequest) {

        String username =
                loginRequest.get("username");

        String password =
                loginRequest.get("password");

        // -------------------------------------------------
        // VALIDATION
        // -------------------------------------------------

        if (username == null ||
                username.trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Username is required."
                            )
                    );
        }

        if (password == null ||
                password.isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Password is required."
                            )
                    );
        }

        username = username.trim();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "LOGIN REQUEST"
        );

        System.out.println(
                "USERNAME = " + username
        );

        try {

            // -------------------------------------------------
            // AUTHENTICATE USER
            // -------------------------------------------------

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    password
                            )
                    );

            System.out.println(
                    "AUTHENTICATION SUCCESS"
            );

            System.out.println(
                    "AUTH NAME = "
                            + authentication.getName()
            );

            System.out.println(
                    "AUTHORITIES = "
                            + authentication.getAuthorities()
            );

            // -------------------------------------------------
            // FIND USER
            // -------------------------------------------------

            User user =
                    userRepository
                            .findByUsername(username)
                            .orElseThrow(
                                    () -> new RuntimeException(
                                            "Authenticated user not found in database."
                                    )
                            );

            // -------------------------------------------------
            // ROLE
            // -------------------------------------------------

            String role =
                    user.getRole();

            if (role == null ||
                    role.trim().isEmpty()) {

                throw new RuntimeException(
                        "User role is empty."
                );
            }

            role =
                    role.trim().toUpperCase();

            if (role.startsWith("ROLE_")) {

                role =
                        role.substring(5);
            }

            System.out.println(
                    "DATABASE ROLE = " + role
            );

            // -------------------------------------------------
            // GENERATE JWT
            // -------------------------------------------------

            String token =
                    jwtService.generateToken(
                            authentication.getName(),
                            role
                    );

            System.out.println(
                    "JWT GENERATED SUCCESSFULLY"
            );

            // -------------------------------------------------
            // RESPONSE
            // -------------------------------------------------

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Login successful."
            );

            response.put(
                    "token",
                    token
            );

            response.put(
                    "username",
                    user.getUsername()
            );

            response.put(
                    "role",
                    role
            );

            System.out.println(
                    "LOGIN RESPONSE ROLE = " + role
            );

            System.out.println(
                    "========================================"
            );

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {

            System.out.println(
                    "BAD CREDENTIALS"
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid username or password."
                            )
                    );

        } catch (AuthenticationException e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "message",
                                    "Authentication failed.",
                                    "error",
                                    e.getMessage() == null
                                            ? "Authentication error"
                                            : e.getMessage()
                            )
                    );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "message",
                                    "Login server error.",
                                    "error",
                                    e.getMessage() == null
                                            ? "Unknown error"
                                            : e.getMessage()
                            )
                    );
        }
    }
}