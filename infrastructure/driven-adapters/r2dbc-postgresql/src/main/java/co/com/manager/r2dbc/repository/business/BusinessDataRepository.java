package co.com.manager.r2dbc.repository.business;

import co.com.manager.r2dbc.data.business.BusinessData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BusinessDataRepository extends ReactiveCrudRepository<BusinessData, String>, ReactiveQueryByExampleExecutor<BusinessData> {

    Flux<BusinessData> findAllByOwnerDocumentId(String userId);
    Mono<BusinessData> findByPhoneNumberId(String phoneNumberId);
    Mono<Void> deleteAllByOwnerDocumentId(String id);
}
