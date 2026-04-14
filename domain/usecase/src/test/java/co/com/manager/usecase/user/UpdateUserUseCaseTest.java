package co.com.manager.usecase.user;

import co.com.manager.model.encoder.EncoderPort;
import co.com.manager.model.exceptions.UserNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

    @Mock
    private UserRepository repository;

    @Mock
    private EncoderPort encoder;

    @InjectMocks
    private UpdateUserUseCase useCase;

    @Test
    void shouldUpdateUserFieldsSuccessfully() {
        String userId = "123";
        User existingUser = User.builder()
                .documentId(userId)
                .name("OldName")
                .surname("OldSurname")
                .email("old@test.com")
                .birthdate("1990-01-01")
                .build();

        User updateData = User.builder()
                .name("NewName")
                .surname("NewSurname")
                .email("new@test.com")
                .birthdate("1995-05-05")
                .build();

        when(repository.findUserById(userId)).thenReturn(Mono.just(existingUser));
        when(repository.update(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.update(userId, updateData))
                .assertNext(user -> {
                    assertEquals("NewName", user.getName());
                    assertEquals("NewSurname", user.getSurname());
                    assertEquals("new@test.com", user.getEmail());
                    assertEquals("1995-05-05", user.getBirthdate());
                    assertNotNull(user.getUpdatedAt());
                })
                .verifyComplete();

        verify(repository).findUserById(userId);
        verify(repository).update(any(User.class));
    }

    @Test
    void shouldThrowUserNotFoundWhenUpdatingNonExistentUser() {
        String userId = "999";
        User updateData = User.builder().name("NewName").build();

        when(repository.findUserById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.update(userId, updateData))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                        throwable.getMessage().contains(userId))
                .verify();

        verify(repository, never()).update(any());
    }

    @Test
    void shouldUpdatePasswordSuccessfully() {
        String userId = "123";
        String newPassword = "newPassword";
        User existingUser = User.builder()
                .documentId(userId)
                .password("oldEncodedPassword")
                .build();

        when(repository.findUserById(userId)).thenReturn(Mono.just(existingUser));
        when(encoder.encodePassword(newPassword)).thenReturn("newEncodedPassword");
        when(repository.update(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.updatePassword(userId, newPassword))
                .assertNext(user -> {
                    assertEquals("newEncodedPassword", user.getPassword());
                    assertNotNull(user.getUpdatedAt());
                })
                .verifyComplete();

        verify(encoder).encodePassword(newPassword);
    }

    @Test
    void shouldThrowUserNotFoundWhenUpdatingPasswordOfNonExistentUser() {
        String userId = "999";

        when(repository.findUserById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updatePassword(userId, "newPassword"))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                        throwable.getMessage().contains(userId))
                .verify();

        verify(encoder, never()).encodePassword(any());
        verify(repository, never()).update(any());
    }
}