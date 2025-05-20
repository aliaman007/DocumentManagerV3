package com.main.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.security.JwtUtil;
import com.main.service.AuthService;

import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        logger.info("Login request received for username: {}", request.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            logger.debug("Authentication successful for username: {}", request.getUsername());
            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(request.getUsername());
                logger.debug("UserDetails loaded: {}", userDetails.getUsername());
            } catch (UsernameNotFoundException e) {
                logger.error("User not found: {}", request.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }
            try {
                String token = jwtUtil.generateToken(userDetails);
                logger.info("JWT generated for username: {}", request.getUsername());
                return ResponseEntity.ok(token);
            } catch (Exception e) {
                logger.error("Failed to generate JWT for username: {}. Error: {}", request.getUsername(), e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("JWT generation failed: " + e.getMessage());
            }
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for username: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (DisabledException e) {
            logger.error("User account disabled: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User account is disabled");
        } catch (Exception e) {
            logger.error("Authentication error for username: {}. Error: {}", request.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication error: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        logger.info("Register request received for username: {}", request.getUsername());
        try {
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                logger.warn("Registration failed: Username is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is required");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                logger.warn("Registration failed: Password is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is required");
            }
            if (request.getRole() == null || request.getRole().trim().isEmpty()) {
                logger.warn("Registration failed: Role is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role is required");
            }
            if (!request.getRole().equals("USER") && !request.getRole().equals("ADMIN")) {
                logger.warn("Registration failed: Invalid role {}", request.getRole());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role must be USER or ADMIN");
            }

            authService.registerUser(request.getUsername(), request.getPassword(), request.getRole());
            logger.info("User registered successfully: {}", request.getUsername());
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Registration failed for username: {}. Error: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Registration error for username: {}. Error: {}", request.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration error: " + e.getMessage());
        }
    }

    // Debug endpoint (optional, keep from prior fixes)
    @GetMapping("/debug/token")
    public ResponseEntity<Map<String, Object>> debugToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtUtil.extractAllClaims(token);
        return ResponseEntity.ok(claims);
    }
}

class LoginRequest {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class RegisterRequest {
    private String username;
    private String password;
    private String role;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}