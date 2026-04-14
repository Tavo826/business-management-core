package co.com.manager.model.business;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BusinessRepository {

    Mono<Business> findBusinessById(String id);
    Mono<Business> findByPhoneNumberId(String phoneNumberId);
    Flux<Business> findAllBusinessByUserId(String userId);
    Mono<Business> create(Business business);
    Mono<Business> update(Business business);
    Mono<Void> delete(String id);
    Mono<Void> deleteAllBusinessByUserId(String userId);
}
