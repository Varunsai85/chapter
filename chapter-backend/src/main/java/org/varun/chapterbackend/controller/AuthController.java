package org.varun.chapterbackend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.varun.chapterbackend.model.User;
import org.varun.chapterbackend.service.AuthService;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class AuthController {
    private AuthService service;

    @PostMapping("signup")
    public ResponseEntity<User> signUp(@RequestBody User user){
        return new ResponseEntity<>(service.addUser(user), HttpStatus.CREATED);
    }
}
