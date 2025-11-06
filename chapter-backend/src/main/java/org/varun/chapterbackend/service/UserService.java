package org.varun.chapterbackend.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.varun.chapterbackend.model.User;
import org.varun.chapterbackend.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private UserRepository repo;

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = repo.findUserByUsername(login);
        if (user == null) {
            user = repo.findUserByEmail(login);
        }
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found 404");
        }

        return user;
    }
}
