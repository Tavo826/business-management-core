package co.com.manager.usecase.user;

import co.com.manager.model.business.BusinessRepository;
import co.com.manager.model.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteUserByIdUseCaseTest {

    @Mock
    private UserRepository repository;

    @Mock
    private BusinessRepository businessRepository;

    @InjectMocks
    private DeleteUserByIdUseCase useCase;

    @Test
    void shouldDeleteUserAndAssociatedBusinesses() {
        String userId = "123";

        when(repository.delete(userId)).thenReturn(Mono.empty());
        when(businessRepository.deleteAllBusinessByUserId(userId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.delete(userId))
                .verifyComplete();

        verify(repository).delete(userId);
        verify(businessRepository).deleteAllBusinessByUserId(userId);
    }

    @Test
    void shouldPropagateErrorWhenUserDeletionFails() {
        String userId = "123";

        when(repository.delete(userId)).thenReturn(Mono.error(new RuntimeException("Delete failed")));

        StepVerifier.create(useCase.delete(userId))
                .expectErrorMessage("Delete failed")
                .verify();
    }

    @Test
    void shouldPropagateErrorWhenBusinessDeletionFails() {
        String userId = "123";

        when(repository.delete(userId)).thenReturn(Mono.empty());
        when(businessRepository.deleteAllBusinessByUserId(userId))
                .thenReturn(Mono.error(new RuntimeException("Business delete failed")));

        StepVerifier.create(useCase.delete(userId))
                .expectErrorMessage("Business delete failed")
                .verify();
    }
}