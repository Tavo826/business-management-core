package co.com.manager.usecase.business;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@AllArgsConstructor
public class CreateBusinessUseCase {

    private final BusinessRepository repository;

    public Mono<Business> create(Business business) {

        return Mono.fromCallable(() -> {
            business.setCreatedAt(LocalDateTime.now());
            return business;
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMap(repository::create);
    }
}
