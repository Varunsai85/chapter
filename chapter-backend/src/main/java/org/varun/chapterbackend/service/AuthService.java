package org.varun.chapterbackend.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private UserRepository repo;
    private BCryptPasswordEncoder encoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    public ResponseEntity<?> signUp(SignUpDto userDto) {
        User existingUser=repo.findUserByEmail(userDto.email());
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
        return new ResponseEntity<>(new ApiResponse<>("User Created Successfully. Please verify your email"),HttpStatus.CREATED);
    }

    public ResponseEntity<?> signIn(SignInDto userDto) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.login(),userDto.password()));

            User user=repo.findUserByEmail(userDto.login());
            if(user==null){
                user=repo.findUserByUsername(userDto.login());
            }

            return new ResponseEntity<>(new ApiResponse<>("Login Succesful",jwtService.generateToken(user.getEmail())),HttpStatus.OK);
        }catch (DisabledException e){
            return new ResponseEntity<>(new ApiResponse<>("User not verified"),HttpStatus.FORBIDDEN);
        }
        catch (BadCredentialsException e){
            return new ResponseEntity<>(new ApiResponse<>("Invalid credentials"),HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e){
            return new ResponseEntity<>(new ApiResponse<>("Something went wrong"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
