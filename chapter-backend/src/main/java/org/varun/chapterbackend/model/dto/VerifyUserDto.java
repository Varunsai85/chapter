package org.varun.chapterbackend.model.dto;

public record VerifyUserDto(
        String email,
        String verificationCode
) {
}
