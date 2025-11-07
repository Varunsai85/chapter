package org.varun.chapterbackend.model.dto;

import jakarta.validation.constraints.NotNull;

public record SignInDto(
        @NotNull(message = "Username or password is required")
        String login,
        @NotNull(message = "Password is required")
        String password
) {
}