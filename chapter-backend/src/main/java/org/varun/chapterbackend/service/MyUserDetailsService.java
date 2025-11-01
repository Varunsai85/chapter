package org.varun.chapterbackend.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.varun.chapterbackend.model.User;
import org.varun.chapterbackend.repository.UserRepository;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=repo.findUserByUsername(username);
        if(user==null){
            throw new UsernameNotFoundException("User Not Found 404");
        }

        return user;
    }
}
