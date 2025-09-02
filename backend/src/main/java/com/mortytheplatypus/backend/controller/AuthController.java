package com.mortytheplatypus.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.mortytheplatypus.backend.model.dto.ApiResponse;
import com.mortytheplatypus.backend.model.dto.LoginDto;
import com.mortytheplatypus.backend.model.dto.RegisterDto;
import com.mortytheplatypus.backend.model.dto.TokenResponse;
import com.mortytheplatypus.backend.service.AuthService;

import lombok.Data;

@Data
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterDto registerDto) {
        try {
            authService.register(registerDto);
            return ResponseEntity.status(201).body(new ApiResponse(true, "User registered successfully"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode().value()).body(new ApiResponse(false, e.getReason()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginDto loginDto) {

        try {
            TokenResponse response = authService.login(loginDto);

            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode().value()).body(new TokenResponse(null));
        }
    }
}
