package co.com.manager.model.token;

import co.com.manager.model.user.User;
import reactor.core.publisher.Mono;

public interface AuthenticationPort {

    Mono<AuthResponse> authenticate(User user);
    Mono<Boolean> validateToken(String token);
    Mono<String> extractMail(String token);
}
