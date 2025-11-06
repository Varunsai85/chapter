package org.varun.chapterbackend.service;

import jakarta.mail.MessagingException;
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
import org.varun.chapterbackend.model.dto.VerifyUserDto;
import org.varun.chapterbackend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@AllArgsConstructor
public class AuthService {
    private UserRepository userRepo;
    private BCryptPasswordEncoder encoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private EmailService emailService;

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
        newUser.setVerificationCode(generateRandomCode());
        newUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));
        newUser.setVerified(false);
        userRepo.save(newUser);
        sendVerificationEmail(newUser);

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

    public ResponseEntity<?> verifyUser(VerifyUserDto input){
        User user=userRepo.findUserByEmail(input.email());
        if(user==null){
            return new ResponseEntity<>(new ApiResponse<>("User not Found"),HttpStatus.NOT_FOUND);
        }
        if(user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())){
            return new ResponseEntity<>(new ApiResponse<>("Verification code expired"),HttpStatus.BAD_REQUEST);
        }
        if(user.getVerificationCode().equals(input.verificationCode())){
            user.setVerified(true);
            user.setVerificationCode(null);
            user.setVerificationCodeExpiresAt(null);
            userRepo.save(user);
            return new ResponseEntity<>(new ApiResponse<>("Account verified Successfully"),HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse<>("Invalid Verification Code"),HttpStatus.UNAUTHORIZED);
        }
    }

    private void sendVerificationEmail(User user){
        String subject="Account Verification";
        String verificationCode="VERIFICATION CODE"+user.getVerificationCode();
        String htmlMessage="<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
        try{
            emailService.sendEmail(user.getEmail(),subject,htmlMessage);
        }catch (MessagingException e){
            e.printStackTrace();
        }
    }

    private String generateRandomCode(){
        Random random=new Random();
        int code=random.nextInt(900000)+100000;
        return String.valueOf(code);
    }
}
