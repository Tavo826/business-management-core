package co.com.manager.passwordencoder;

import co.com.manager.model.encoder.EncoderPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PasswordEncoder implements EncoderPort {

    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public PasswordEncoder(org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public Mono<Boolean> verifyPassword(String inputPassword, String storedPassword) {

        return Mono.just(passwordEncoder.matches(inputPassword, storedPassword));
    }
}
