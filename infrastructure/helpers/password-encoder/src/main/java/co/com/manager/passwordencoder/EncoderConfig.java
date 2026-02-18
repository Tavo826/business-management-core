package co.com.manager.passwordencoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebFluxSecurity
public class EncoderConfig {

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder springPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
