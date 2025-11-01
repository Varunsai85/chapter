package org.varun.chapterbackend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.varun.chapterbackend.model.User;
import org.varun.chapterbackend.model.dto.SignUpDto;
import org.varun.chapterbackend.service.AuthService;
import org.varun.chapterbackend.service.JwtService;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class AuthController {
    private AuthService service;
    private JwtService jwtService;

    @PostMapping("signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto userDto) {
        User existingUser = service.getUserByEmail(userDto.email());
        if (existingUser != null) {
            if (existingUser.isVerified()) {
                return new ResponseEntity<>("User Already exists and Verified", HttpStatus.BAD_REQUEST);
            } else {
                existingUser.setVerificationCode(jwtService.generateToken(existingUser.getEmail()));
                service.updateUser(existingUser);
                //TODO
                // SEND EMAIL
                return new ResponseEntity<>("Verification Code is resent", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(service.addUser(userDto, jwtService.generateToken(userDto.email())), HttpStatus.CREATED);
    }
}
