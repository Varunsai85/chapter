package org.varun.chapterbackend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.varun.chapterbackend.model.dto.SignUpDto;
import org.varun.chapterbackend.service.AuthService;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class AuthController {
    private AuthService service;

    @PostMapping("signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto userDto) {
        return service.signUp(userDto);
    }
}
