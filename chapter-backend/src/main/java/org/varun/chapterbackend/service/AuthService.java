package org.varun.chapterbackend.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.varun.chapterbackend.model.User;
import org.varun.chapterbackend.model.dto.ApiResponse;
import org.varun.chapterbackend.model.dto.SignInDto;
import org.varun.chapterbackend.model.dto.SignUpDto;
import org.varun.chapterbackend.repository.UserRepository;

@Service
@AllArgsConstructor
public class AuthService {
    private UserRepository userRepo;
    private BCryptPasswordEncoder encoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    public ResponseEntity<?> signUp(SignUpDto userDto) {
        User existingUser = userRepo.findUserByEmail(userDto.email());
        if (existingUser != null) {
            if (existingUser.isVerified()) {
                return new ResponseEntity<>(new ApiResponse<>("User already exists"), HttpStatus.BAD_GATEWAY);
            }
            return new ResponseEntity<>(new ApiResponse<>("User not verified, Please verify"), HttpStatus.FORBIDDEN);
        }
        User newUser = new User();
        newUser.setEmail(userDto.email());
        newUser.setUsername(userDto.username());
        newUser.setPassword(encoder.encode(userDto.password()));
        newUser.setVerificationCode("Random");
        newUser.setVerified(false);
        userRepo.save(newUser);
        return new ResponseEntity<>(new ApiResponse<>("User created successfully, Please verify"), HttpStatus.CREATED);
    }

    public ResponseEntity<?> signIn(SignInDto userDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.subject(), userDto.password()));
            User user = userRepo.findUserByUsername(userDto.subject());
            if (user == null) user = userRepo.findUserByEmail(userDto.subject());

            final String jwtToken = jwtService.generateToken(user.getUsername());
            return new ResponseEntity<>(new ApiResponse<>("Login Successful", jwtToken), HttpStatus.OK);
        } catch (DisabledException e) {
            return new ResponseEntity<>(new ApiResponse<>("User not verified"), HttpStatus.FORBIDDEN);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ApiResponse<>("Invalid Credentials"), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Something went wrong"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
