package org.varun.chapterbackend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.varun.chapterbackend.model.User;
import org.varun.chapterbackend.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private UserRepository repo;

    public List<User> getAllUsers() {
        return repo.findAll();
    }
}
