package co.com.manager.usecase.business;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessRepository;
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
class CreateBusinessUseCaseTest {

    @Mock
    private BusinessRepository repository;

    @InjectMocks
    private CreateBusinessUseCase useCase;

    @Test
    void shouldCreateBusinessSuccessfully() {
        Business business = Business.builder()
                .nit("900123456")
                .name("My Business")
                .description("A test business")
                .ownerDocumentId("123")
                .build();

        when(repository.create(business)).thenReturn(Mono.just(business));

        StepVerifier.create(useCase.create(business))
                .assertNext(created -> {
                    assertEquals("900123456", created.getNit());
                    assertEquals("My Business", created.getName());
                })
                .verifyComplete();

        verify(repository).create(business);
    }

    @Test
    void shouldPropagateErrorWhenRepositoryFails() {
        Business business = Business.builder().nit("900123456").build();

        when(repository.create(business)).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(useCase.create(business))
                .expectErrorMessage("DB error")
                .verify();
    }
}