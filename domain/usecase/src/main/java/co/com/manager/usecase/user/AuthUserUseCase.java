package co.com.manager.usecase.user;

import co.com.manager.model.encoder.EncoderPort;
import co.com.manager.model.exceptions.InvalidPasswordException;
import co.com.manager.model.exceptions.UserNotFoundException;
import co.com.manager.model.token.AuthCredentials;
import co.com.manager.model.token.AuthenticationPort;
import co.com.manager.model.token.AuthResponse;
import co.com.manager.model.user.UserRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class AuthUserUseCase {

    private final UserRepository userRepository;
    private final AuthenticationPort authenticationPort;
    private final EncoderPort encoderPort;

    public Mono<AuthResponse> authenticate(AuthCredentials credentials) {

        return userRepository.findUserByEmail(credentials.getEmail())
                .switchIfEmpty(Mono.error(new UserNotFoundException("[email: " + credentials.getEmail() + "]")))
                .filterWhen(user -> encoderPort.verifyPassword(credentials.getPassword(), user.getPassword()))
                .switchIfEmpty(Mono.error(new InvalidPasswordException()))
                .flatMap(authenticationPort::authenticate);
    }
}
