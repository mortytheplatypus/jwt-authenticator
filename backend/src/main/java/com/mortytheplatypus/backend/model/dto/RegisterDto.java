package com.mortytheplatypus.backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDto(
        @NotBlank @Size(min = 3, message = "Name must be at least 3 characters long") String name,
        @NotBlank @Email String email,
        @NotBlank String username,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters long") String password
) {
}
