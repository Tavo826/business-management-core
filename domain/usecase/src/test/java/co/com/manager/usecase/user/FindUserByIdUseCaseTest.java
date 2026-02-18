package co.com.manager.usecase.user;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindUserByIdUseCaseTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private FindUserByIdUseCase useCase;

    @Test
    void shouldReturnUserWhenFound() {
        String userId = "123";
        User expectedUser = User.builder()
                .documentId(userId)
                .name("John")
                .email("john@test.com")
                .build();

        when(repository.findUserById(userId)).thenReturn(Mono.just(expectedUser));

        StepVerifier.create(useCase.findById(userId))
                .assertNext(user -> {
                    assertEquals(userId, user.getDocumentId());
                    assertEquals("John", user.getName());
                })
                .verifyComplete();

        verify(repository).findUserById(userId);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        String userId = "999";

        when(repository.findUserById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.findById(userId))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                        throwable.getMessage().contains(userId))
                .verify();
    }
}