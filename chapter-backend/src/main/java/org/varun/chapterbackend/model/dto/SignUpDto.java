package org.varun.chapterbackend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SignUpDto(
        @NotNull(message = "Username is required")
        String username,
        @NotNull(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        @NotNull(message = "Password is required")
        String password
) {
}
