package org.varun.chapterbackend.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.varun.chapterbackend.service.MyUserDetailsService;

@Configuration
@AllArgsConstructor
public class ApplicationConfig {
    private MyUserDetailsService userDetailsService;
    private BCryptPasswordEncoder encoder;

    @Bean
    public AuthenticationProvider authProvider(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(encoder);

        return provider;
    }
}
