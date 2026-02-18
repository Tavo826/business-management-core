package co.com.manager.usecase.user;

import co.com.manager.model.encoder.EncoderPort;
import co.com.manager.model.token.AuthenticationPort;
import co.com.manager.model.token.AuthResponse;
import co.com.manager.model.user.User;
import co.com.manager.model.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private UserRepository repository;

    @Mock
    private EncoderPort encoder;

    @Mock
    private AuthenticationPort authentication;

    @InjectMocks
    private CreateUserUseCase useCase;

    @Test
    void shouldCreateUserAndReturnToken() {
        User user = User.builder()
                .documentId("123")
                .name("John")
                .email("john@test.com")
                .password("rawPassword")
                .build();

        AuthResponse expectedToken = AuthResponse.builder().token("jwt-token").build();

        when(encoder.encodePassword("rawPassword")).thenReturn("encodedPassword");
        when(repository.create(any(User.class))).thenReturn(Mono.just(user));
        when(authentication.authenticate(any(User.class))).thenReturn(Mono.just(expectedToken));

        StepVerifier.create(useCase.create(user))
                .assertNext(token -> assertEquals("jwt-token", token.getToken()))
                .verifyComplete();

        assertEquals("encodedPassword", user.getPassword());
        assertNotNull(user.getCreatedAt());
        verify(encoder).encodePassword("rawPassword");
        verify(repository).create(user);
        verify(authentication).authenticate(user);
    }

    @Test
    void shouldPropagateErrorWhenRepositoryFails() {
        User user = User.builder()
                .documentId("123")
                .password("rawPassword")
                .build();

        when(encoder.encodePassword("rawPassword")).thenReturn("encodedPassword");
        when(repository.create(any(User.class))).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(useCase.create(user))
                .expectErrorMessage("DB error")
                .verify();

        verify(authentication, never()).authenticate(any());
    }

    @Test
    void shouldPropagateErrorWhenAuthenticationFails() {
        User user = User.builder()
                .documentId("123")
                .password("rawPassword")
                .build();

        when(encoder.encodePassword("rawPassword")).thenReturn("encodedPassword");
        when(repository.create(any(User.class))).thenReturn(Mono.just(user));
        when(authentication.authenticate(any(User.class)))
                .thenReturn(Mono.error(new RuntimeException("Auth error")));

        StepVerifier.create(useCase.create(user))
                .expectErrorMessage("Auth error")
                .verify();
    }
}