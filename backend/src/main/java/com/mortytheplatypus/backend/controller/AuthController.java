package com.mortytheplatypus.backend.controller;

import com.mortytheplatypus.backend.model.dto.ApiResponse;
import com.mortytheplatypus.backend.model.dto.LoginDto;
import com.mortytheplatypus.backend.model.dto.RegisterDto;
import com.mortytheplatypus.backend.model.dto.TokenResponse;
import com.mortytheplatypus.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterDto registerDto) {
        authService.register(registerDto);
        return ResponseEntity.status(201).body(new ApiResponse(true, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(authService.login(loginDto));
    }
}
