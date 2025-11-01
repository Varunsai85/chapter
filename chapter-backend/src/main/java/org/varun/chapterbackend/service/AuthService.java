package org.varun.chapterbackend.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.varun.chapterbackend.model.User;
import org.varun.chapterbackend.model.dto.SignUpDto;
import org.varun.chapterbackend.repository.UserRepository;

@Service
@AllArgsConstructor
public class AuthService {
    private UserRepository repo;
    private BCryptPasswordEncoder encoder;

    public User addUser(SignUpDto userDto,String verificationCode) {
        User user=new User();
        user.setEmail(userDto.email());
        user.setUsername(userDto.username());
        user.setPassword(encoder.encode(userDto.password()));
        user.setVerificationCode(verificationCode);
        user.setVerified(false);
        return repo.save(user);
    }

    public User updateUser(User user){
        return repo.save(user);
    }

    public User getUserByEmail(String email) {
        return repo.findByEmail(email);
    }
}
