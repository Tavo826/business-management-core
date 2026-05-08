package co.com.manager.r2dbc.repository.order;

import co.com.manager.r2dbc.data.order.OrderData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderDataRepository extends ReactiveCrudRepository<OrderData, String>, ReactiveQueryByExampleExecutor<OrderData> {

    Flux<OrderData> findByBusinessIdOrderByCreatedAtDesc(String businessId);

    Flux<OrderData> findAllByBusinessIdAndStatus(String businessId, String status);
}
