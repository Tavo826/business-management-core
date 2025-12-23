package co.com.manager.usecase.business;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class CreateBusinessUseCase {

    private final BusinessRepository repository;

    public Mono<Business> create(Business business) {

        return repository.create(business);
    }
}
