package co.com.manager.usecase.validation;

import co.com.manager.model.token.TokenValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ValidateWebhookTokenUseCase {

    private final TokenValidator tokenValidator;

    public Mono<String> verifyToken(String challenge, String token) {

        if (tokenValidator.validate(token)) {
            return Mono.just(challenge);
        }

        return Mono.empty();
    }
}
