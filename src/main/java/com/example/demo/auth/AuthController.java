package com.example.demo.auth;

import com.example.demo.auth.model.LoginRequest;
import com.example.demo.auth.model.LoginResponse;
import com.example.demo.auth.model.SignupRequest;
import com.example.demo.employee.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
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
}
