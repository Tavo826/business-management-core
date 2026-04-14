package co.com.manager.r2dbc.repository.order;

import co.com.manager.r2dbc.data.order.OrderItemData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderItemDataRepository extends ReactiveCrudRepository<OrderItemData, String>, ReactiveQueryByExampleExecutor<OrderItemData> {

    Flux<OrderItemData> findByOrderId(String orderId);
}
