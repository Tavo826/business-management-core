package co.com.manager.r2dbc.repository.consent;

import co.com.manager.r2dbc.data.consent.ConsentData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ConsentDataRepository extends ReactiveCrudRepository<ConsentData, String>, ReactiveQueryByExampleExecutor<ConsentData> {

    Mono<Boolean> existsByBusinessNitAndCustomerPhone(String businessNit, String customerPhone);
}
