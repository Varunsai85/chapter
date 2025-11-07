package org.varun.chapterbackend.service;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.varun.chapterbackend.model.dto.VerifyCodeDto;
import org.varun.chapterbackend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {
    private UserRepository userRepo;
    private BCryptPasswordEncoder encoder;
    private EmailService emailService;
    private AuthenticationManager authManager;
    private JwtService jwtService;

    public ResponseEntity<?> signUp(@Valid SignUpDto input) {
        Optional<User> optionalUser = userRepo.findUserByEmail(input.email());
        if (optionalUser.isPresent()) {
            User existingUser=optionalUser.get();
            if (existingUser.isEnabled()) {
                return new ResponseEntity<>(new ApiResponse<>("Use Already exists"), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(new ApiResponse<>("User exists but not verified, Please verify your account"), HttpStatus.UNAUTHORIZED);
        }
        User newUser = new User(
                input.username(),
                input.email(),
                encoder.encode(input.password()),
                false,
                generateVerificationCode(),
                LocalDateTime.now().plusMinutes(10)
        );
        userRepo.save(newUser);
        try {
            sendVerificationEmail(newUser);
            return new ResponseEntity<>(new ApiResponse<>("User Created successful, Verification mail has been sent to your email"), HttpStatus.CREATED);
        }catch (MessagingException e){
            log.error("Verification email failed for {} {}",newUser.getEmail(),e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("User created but sending verification email failed"),HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception e){
            log.error("Something went wrong {}",e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Something went wrong"),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<?> signIn(@Valid SignInDto input) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(input.login(), input.password()));
            String jwtToken = jwtService.generateToken(input.login());
            return new ResponseEntity<>(new ApiResponse<>("User logged in successfully", jwtToken), HttpStatus.OK);
        } catch (DisabledException e) {
            return new ResponseEntity<>(new ApiResponse<>("Please verify your account", e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ApiResponse<>("Invalid credentials", e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Something went wrong", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> verifyUser(@Valid VerifyCodeDto input) {
        Optional<User> optUser=userRepo.findUserByEmail(input.email());
        if(optUser.isEmpty()){
            return new ResponseEntity<>(new ApiResponse<>("User Not Found"),HttpStatus.NOT_FOUND);
        }
        User user=optUser.get();
        if(user.getCodeExpiresIn().isBefore(LocalDateTime.now())){
            return new ResponseEntity<>(new ApiResponse<>("Verification Code expired"),HttpStatus.FORBIDDEN);
        }
        if(user.getVerificationCode().equals(input.code())){
            user.setVerificationCode(null);
            user.setCodeExpiresIn(null);
            user.setEnabled(true);
            userRepo.save(user);
            return new ResponseEntity<>(new ApiResponse<>("User verified successfully"),HttpStatus.OK);
        }else {
            return new ResponseEntity<>(new ApiResponse<>("Invalid verification code"),HttpStatus.UNAUTHORIZED);
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private void sendVerificationEmail(User user) throws MessagingException{
        String subject = "Account Verification";
        String verificationCode = "Verification Code "+user.getVerificationCode();
        String htmlText = "<html>"
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
        emailService.sendVerificationEmail(user.getEmail(), subject, htmlText);
    }

    public ResponseEntity<ApiResponse<Boolean>> checkUserName(String username) {
        boolean exists=userRepo.findUserByUsername(username).isPresent();
        if(exists){
            return new ResponseEntity<>(new ApiResponse<>("Username Already exists",false),HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse<>("Username available",true),HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<Boolean>> checkEmail(String email) {
        boolean exists=userRepo.findUserByEmail(email).isPresent();
        if(exists){
            return new ResponseEntity<>(new ApiResponse<>("Email Already exists",false),HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse<>("Email available",true),HttpStatus.OK);
    }
}