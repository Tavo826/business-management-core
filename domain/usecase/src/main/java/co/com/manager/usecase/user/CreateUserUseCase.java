package co.com.manager.usecase.user;

import co.com.manager.model.encoder.EncoderPort;
import co.com.manager.model.token.AuthResponse;
import co.com.manager.model.token.AuthenticationPort;
import co.com.manager.model.user.User;
import co.com.manager.model.user.UserRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@AllArgsConstructor
public class CreateUserUseCase {

    private final UserRepository repository;
    private final EncoderPort encoder;
    private final AuthenticationPort authentication;

    public Mono<AuthResponse> create(User user) {

        return Mono.fromCallable(() -> {
            user.setPassword(encoder.encodePassword(user.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            return user;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(repository::create)
        .flatMap(authentication::authenticate);
    }
}
