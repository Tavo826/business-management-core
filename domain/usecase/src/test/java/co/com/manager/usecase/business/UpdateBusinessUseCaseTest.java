package co.com.manager.usecase.business;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessRepository;
import co.com.manager.model.exceptions.BusinessNotFoundException;
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
class UpdateBusinessUseCaseTest {

    @Mock
    private BusinessRepository repository;

    @InjectMocks
    private UpdateBusinessUseCase useCase;

    @Test
    void shouldUpdateBusinessFieldsSuccessfully() {
        String businessId = "900123456";
        Business existingBusiness = Business.builder()
                .nit(businessId)
                .name("Old Name")
                .description("Old Description")
                .phone("1111111")
                .email("old@test.com")
                .address("Old Address")
                .build();

        Business updateData = Business.builder()
                .name("New Name")
                .description("New Description")
                .phone("2222222")
                .email("new@test.com")
                .address("New Address")
                .build();

        when(repository.findBusinessById(businessId)).thenReturn(Mono.just(existingBusiness));
        when(repository.update(any(Business.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.update(businessId, updateData))
                .assertNext(business -> {
                    assertEquals("New Name", business.getName());
                    assertEquals("New Description", business.getDescription());
                    assertEquals("2222222", business.getPhone());
                    assertEquals("new@test.com", business.getEmail());
                    assertEquals("New Address", business.getAddress());
                })
                .verifyComplete();

        verify(repository).findBusinessById(businessId);
        verify(repository).update(any(Business.class));
    }

    @Test
    void shouldThrowBusinessNotFoundWhenUpdatingNonExistentBusiness() {
        String businessId = "999";
        Business updateData = Business.builder().name("New Name").build();

        when(repository.findBusinessById(businessId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.update(businessId, updateData))
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessNotFoundException &&
                        throwable.getMessage().contains(businessId))
                .verify();

        verify(repository, never()).update(any());
    }
}