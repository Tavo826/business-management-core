package co.com.manager.usecase.user;

import co.com.manager.model.encoder.EncoderPort;
import co.com.manager.model.exceptions.InvalidPasswordException;
import co.com.manager.model.exceptions.UserNotFoundException;
import co.com.manager.model.token.AuthCredentials;
import co.com.manager.model.token.AuthResponse;
import co.com.manager.model.token.AuthenticationPort;
import co.com.manager.model.user.User;
import co.com.manager.model.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationPort authenticationPort;

    @Mock
    private EncoderPort encoderPort;

    @InjectMocks
    private AuthUserUseCase useCase;

    @Test
    void shouldAuthenticateSuccessfully() {
        AuthCredentials credentials = AuthCredentials.builder()
                .email("john@test.com")
                .password("rawPassword")
                .build();

        User storedUser = User.builder()
                .documentId("123")
                .email("john@test.com")
                .password("encodedPassword")
                .build();

        AuthResponse expectedToken = AuthResponse.builder().token("jwt-token").build();

        when(userRepository.findUserByEmail("john@test.com")).thenReturn(Mono.just(storedUser));
        when(encoderPort.verifyPassword("rawPassword", "encodedPassword")).thenReturn(Mono.just(true));
        when(authenticationPort.authenticate(storedUser)).thenReturn(Mono.just(expectedToken));

        StepVerifier.create(useCase.authenticate(credentials))
                .assertNext(token -> assertEquals("jwt-token", token.getToken()))
                .verifyComplete();

        verify(userRepository).findUserByEmail("john@test.com");
        verify(encoderPort).verifyPassword("rawPassword", "encodedPassword");
        verify(authenticationPort).authenticate(storedUser);
    }

    @Test
    void shouldThrowUserNotFoundWhenEmailDoesNotExist() {
        AuthCredentials credentials = AuthCredentials.builder()
                .email("unknown@test.com")
                .password("password")
                .build();

        when(userRepository.findUserByEmail("unknown@test.com")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.authenticate(credentials))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                        throwable.getMessage().contains("unknown@test.com"))
                .verify();

        verify(encoderPort, never()).verifyPassword(any(), any());
        verify(authenticationPort, never()).authenticate(any());
    }

    @Test
    void shouldThrowInvalidPasswordWhenPasswordDoesNotMatch() {
        AuthCredentials credentials = AuthCredentials.builder()
                .email("john@test.com")
                .password("wrongPassword")
                .build();

        User storedUser = User.builder()
                .documentId("123")
                .email("john@test.com")
                .password("encodedPassword")
                .build();

        when(userRepository.findUserByEmail("john@test.com")).thenReturn(Mono.just(storedUser));
        when(encoderPort.verifyPassword("wrongPassword", "encodedPassword")).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.authenticate(credentials))
                .expectError(InvalidPasswordException.class)
                .verify();

        verify(authenticationPort, never()).authenticate(any());
    }
}