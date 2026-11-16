package com.example.projectdnd.controller;

import com.example.projectdnd.model.dto.AuthResponse;
import com.example.projectdnd.model.dto.LoginRequest;
import com.example.projectdnd.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for handling user authentication requests.
 * Exposes endpoints under {@code /api/auth} for logging in and receiving JWT tokens.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Constructs an {@code AuthController} with the required authentication dependencies.
     *
     * @param authenticationManager Spring Security authentication manager for verifying credentials
     * @param jwtService            service for generating signed JWT tokens
     */
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Authenticates a user with a username and password and issues a JWT token.
     *
     * @param request the login payload containing the username and plaintext password
     * @return a {@link ResponseEntity} containing the generated {@link AuthResponse} JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Authenticate credentials against PostgreSQL using BCrypt
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Generate and return JWT token upon successful authentication
        String token = jwtService.generateToken(request.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}