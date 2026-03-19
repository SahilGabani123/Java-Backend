package com.example.demo.auth;

import com.example.demo.auth.model.LoginRequest;
import com.example.demo.auth.model.SignupRequest;
import com.example.demo.security.JwtService;
import com.example.demo.security.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testRevokeToken_ValidToken() throws Exception {
        // Given
        String validToken = "eyJhbGciOiJIUzI1NiJ9.validToken";
        Date expirationDate = new Date(System.currentTimeMillis() + 3600000); // 1 hour from now

        when(jwtService.getExpiration(validToken)).thenReturn(expirationDate);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/revoke")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token revoked successfully."));

        // Verify
        verify(tokenBlacklistService, times(1)).blacklist(validToken, expirationDate);
        verify(jwtService, times(1)).getExpiration(validToken);
    }

    @Test
    void testRevokeToken_ExpiredToken() throws Exception {
        // Given
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.expiredToken";
        Date expirationDate = new Date(System.currentTimeMillis() - 3600000); // 1 hour ago

        when(jwtService.getExpiration(expiredToken)).thenThrow(new RuntimeException("Token expired"));
        when(jwtService.getExpirationFromExpiredToken(expiredToken)).thenReturn(expirationDate);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/revoke")
                        .header("Authorization", "Bearer " + expiredToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token revoked successfully."));

        // Verify
        verify(tokenBlacklistService, times(1)).blacklist(expiredToken, expirationDate);
        verify(jwtService, times(1)).getExpiration(expiredToken);
        verify(jwtService, times(1)).getExpirationFromExpiredToken(expiredToken);
    }

    @Test
    void testRevokeToken_NoAuthorizationHeader() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/auth/revoke")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No authorization header provided."));
    }

    @Test
    void testRevokeToken_InvalidToken() throws Exception {
        // Given
        String invalidToken = "invalid.token.format";

        when(jwtService.getExpiration(invalidToken)).thenThrow(new RuntimeException("Invalid token"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/revoke")
                        .header("Authorization", "Bearer " + invalidToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid token: Invalid token"));
    }

    @Test
    void testRevokeToken_MalformedAuthorizationHeader() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/auth/revoke")
                        .header("Authorization", "InvalidFormat")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No authorization header provided."));
    }
}