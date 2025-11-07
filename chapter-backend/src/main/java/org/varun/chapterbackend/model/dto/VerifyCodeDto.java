package org.varun.chapterbackend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record VerifyCodeDto(
        @NotNull(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        @NotNull(message = "Verification code is required")
        String code
) {
}
