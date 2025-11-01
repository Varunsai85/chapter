package org.varun.chapterbackend.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.varun.chapterbackend.model.User;
import org.varun.chapterbackend.model.dto.ApiResponse;
import org.varun.chapterbackend.model.dto.SignUpDto;
import org.varun.chapterbackend.repository.UserRepository;

@Service
@AllArgsConstructor
public class AuthService {
    private UserRepository repo;
    private BCryptPasswordEncoder encoder;
    private JwtService jwtService;

    public ResponseEntity<?> signUp(SignUpDto userDto) {
        User existingUser=repo.findByEmail(userDto.email());
        if(existingUser!=null){
            if(existingUser.isVerified()){
                return new ResponseEntity<>(new ApiResponse<>("User Already exists and Verified"),HttpStatus.BAD_GATEWAY);
            }else{
                existingUser.setVerificationCode(jwtService.generateToken(existingUser.getEmail()));
                repo.save(existingUser);
                //ToDo
                // Send Email
                return new ResponseEntity<>(new ApiResponse<>("User Not Verfied verification code resent"),HttpStatus.OK);
            }
        }
        User user = new User();
        user.setEmail(userDto.email());
        user.setPassword(encoder.encode(userDto.password()));
        user.setUsername(userDto.username());
        user.setVerificationCode(jwtService.generateToken(userDto.email()));
        user.setVerified(false);
        return new ResponseEntity<>(new ApiResponse<>("User Created Sucessfully. Please verify your email"),HttpStatus.CREATED);
    }
}
