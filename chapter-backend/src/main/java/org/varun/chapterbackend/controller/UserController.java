package org.varun.chapterbackend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.varun.chapterbackend.model.User;
import org.varun.chapterbackend.service.UserService;

import java.util.List;

@RestController
@RequestMapping("user")
@AllArgsConstructor
public class UserController {
    private UserService service;

    @GetMapping("users")
    public ResponseEntity<List<User>> getUsers() {
        return new ResponseEntity<>(service.getAllUsers(), HttpStatus.OK);
    }
}
