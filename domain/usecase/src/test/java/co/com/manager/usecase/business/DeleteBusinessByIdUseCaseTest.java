package co.com.manager.usecase.business;

import co.com.manager.model.business.BusinessRepository;
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
class DeleteBusinessByIdUseCaseTest {

    @Mock
    private BusinessRepository repository;

    @InjectMocks
    private DeleteBusinessByIdUseCase useCase;

    @Test
    void shouldDeleteBusinessSuccessfully() {
        String businessId = "900123456";

        when(repository.delete(businessId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.delete(businessId))
                .verifyComplete();

        verify(repository).delete(businessId);
    }

    @Test
    void shouldPropagateErrorWhenDeletionFails() {
        String businessId = "900123456";

        when(repository.delete(businessId)).thenReturn(Mono.error(new RuntimeException("Delete failed")));

        StepVerifier.create(useCase.delete(businessId))
                .expectErrorMessage("Delete failed")
                .verify();
    }
}