package co.com.manager.model.encoder;

import reactor.core.publisher.Mono;

public interface EncoderPort {

    String encodePassword(String password);
    Mono<Boolean> verifyPassword(String inputPassword, String storedPassword);
}
