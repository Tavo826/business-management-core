package co.com.manager.usecase.business;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessRepository;
import co.com.manager.model.exceptions.BusinessNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindBusinessUseCaseTest {

    @Mock
    private BusinessRepository repository;

    @InjectMocks
    private FindBusinessUseCase useCase;

    @Test
    void shouldReturnBusinessWhenFoundById() {
        String businessId = "900123456";
        Business expected = Business.builder()
                .nit(businessId)
                .name("My Business")
                .build();

        when(repository.findBusinessById(businessId)).thenReturn(Mono.just(expected));

        StepVerifier.create(useCase.findById(businessId))
                .assertNext(business -> {
                    assertEquals(businessId, business.getNit());
                    assertEquals("My Business", business.getName());
                })
                .verifyComplete();

        verify(repository).findBusinessById(businessId);
    }

    @Test
    void shouldThrowBusinessNotFoundWhenIdDoesNotExist() {
        String businessId = "999";

        when(repository.findBusinessById(businessId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.findById(businessId))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessNotFoundException &&
                        throwable.getMessage().contains(businessId))
                .verify();
    }

    @Test
    void shouldReturnAllBusinessesByUserId() {
        String userId = "123";
        Business business1 = Business.builder().nit("001").name("Business 1").build();
        Business business2 = Business.builder().nit("002").name("Business 2").build();

        when(repository.findAllBusinessByUserId(userId)).thenReturn(Flux.just(business1, business2));

        StepVerifier.create(useCase.findAllByUserId(userId))
                .assertNext(b -> assertEquals("Business 1", b.getName()))
                .assertNext(b -> assertEquals("Business 2", b.getName()))
                .verifyComplete();

        verify(repository).findAllBusinessByUserId(userId);
    }

    @Test
    void shouldThrowBusinessNotFoundWhenNoBusinessesForUser() {
        String userId = "999";

        when(repository.findAllBusinessByUserId(userId)).thenReturn(Flux.empty());

        StepVerifier.create(useCase.findAllByUserId(userId))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessNotFoundException &&
                        throwable.getMessage().contains(userId))
                .verify();
    }
}