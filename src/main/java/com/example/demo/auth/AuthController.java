package com.example.demo.auth;

import com.example.demo.auth.model.LoginRequest;
import com.example.demo.auth.model.LoginResponse;
import com.example.demo.auth.model.SignupRequest;
import com.example.demo.employee.ApiResponse;
import com.example.demo.security.JwtService;
import com.example.demo.security.TokenBlacklistService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(UserService userService, JwtService jwtService, TokenBlacklistService tokenBlacklistService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("auth/signup")
    public ResponseEntity<ApiResponse> signup(
            @Valid @RequestBody SignupRequest request) {

        User user = userService.signup(request);

        return ResponseEntity.ok(
                new ApiResponse(true, "User registered successfully")
        );
    }

    @PostMapping("auth/login")
    public ResponseEntity<ApiResponse> login(
            @Valid @RequestBody LoginRequest request) {

        String token = userService.login(request);
        User user = userService.getUserByEmail(request.getEmail());

        LoginResponse response = new LoginResponse("Bearer " + token, user);

        return ResponseEntity.ok(
                new ApiResponse(true, "Login successful", response)
        );
    }

    @PostMapping("auth/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Blacklist the token so it cannot be used again
            tokenBlacklistService.blacklist(token, jwtService.getExpiration(token));
        }

        return ResponseEntity.ok(
                new ApiResponse(true, "Logout successful.")
        );
    }

    @PostMapping("auth/revoke")
    public ResponseEntity<ApiResponse> revokeToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                // For expired tokens, we need to extract expiration from the expired token itself
                Date expiration;
                try {
                    // Try to get expiration from valid token first
                    expiration = jwtService.getExpiration(token);
                } catch (Exception e) {
                    // If token is expired, extract expiration from expired token
                    expiration = jwtService.getExpirationFromExpiredToken(token);
                }
                
                // Blacklist the token so it cannot be used again
                tokenBlacklistService.blacklist(token, expiration);
                
                return ResponseEntity.ok(
                        new ApiResponse(true, "Token revoked successfully.")
                );
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Invalid token: " + e.getMessage()));
            }
        }

        return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "No authorization header provided."));
    }
}
