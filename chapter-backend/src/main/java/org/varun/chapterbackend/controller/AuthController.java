package org.varun.chapterbackend.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.varun.chapterbackend.model.dto.ApiResponse;
import org.varun.chapterbackend.model.dto.SignInDto;
import org.varun.chapterbackend.model.dto.SignUpDto;
import org.varun.chapterbackend.model.dto.VerifyCodeDto;
import org.varun.chapterbackend.service.AuthService;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

    @PostMapping("signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpDto input){
        return authService.signUp(input);
    }

    @PostMapping("signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInDto input){
        return authService.signIn(input);
    }

    @PostMapping("verify")
    public ResponseEntity<?> verify(@Valid @RequestParam VerifyCodeDto input){
        return authService.verifyUser(input);
    }

    @GetMapping("check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@RequestParam String username){
        return authService.checkUserName(username);
    }

    @GetMapping("check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email){
        return authService.checkEmail(email);
    }

}
