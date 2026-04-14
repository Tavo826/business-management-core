package co.com.manager.model.order;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository {

    Mono<Order> saveOrder(Order order);

    Mono<Order> findById(String id);

    Flux<Order> findAllByBusinessId(String businessId);

    Flux<Order> findByStatus(String businessId, String status);

    Mono<Order> updateStatus(Order order);
}
