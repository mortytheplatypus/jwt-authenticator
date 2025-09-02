package com.mortytheplatypus.backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public record RegisterDto(
    @NotBlank String name, 
    @NotBlank String email, 
    @NotBlank @Email String username, 
    @NotBlank String password) {
}
