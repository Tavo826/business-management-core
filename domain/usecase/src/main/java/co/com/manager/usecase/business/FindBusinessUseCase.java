package co.com.manager.usecase.business;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessRepository;
import co.com.manager.model.exceptions.BusinessNotFoundException;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class FindBusinessUseCase {

    private final BusinessRepository repository;

    public Mono<Business> findById(String id) {

        return repository.findBusinessById(id)
                .switchIfEmpty(Mono.error(new BusinessNotFoundException(id)));
    }

    public Flux<Business> findAllByUserId(String userId) {

        return repository.findAllBusinessByUserId(userId)
                .switchIfEmpty(Mono.error(new BusinessNotFoundException("user:" + userId)));
    }
}
