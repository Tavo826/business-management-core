package co.com.manager.usecase.validation;

import co.com.manager.model.token.TokenValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RequiredArgsConstructor
public class ValidateWebhookTokenUseCase {

    private final TokenValidator tokenValidator;

    public Mono<String> verifyToken(Optional<String> challenge, Optional<String> token) {

        if (tokenValidator.validate(token.get())) {
            return Mono.just(challenge.orElse(""));
        }

        return Mono.empty();
    }
}
