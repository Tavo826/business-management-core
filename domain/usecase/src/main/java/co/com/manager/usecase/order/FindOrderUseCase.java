package co.com.manager.usecase.order;

import co.com.manager.model.exceptions.OrderNotFoundException;
import co.com.manager.model.order.Order;
import co.com.manager.model.order.OrderRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class FindOrderUseCase {

    private final OrderRepository orderRepository;

    public Flux<Order> findAllByBusinessId(String businessId) {
        return orderRepository.findAllByBusinessId(businessId)
                .switchIfEmpty(Mono.error(new OrderNotFoundException("Business Id: " + businessId)));
    }

    public Mono<Order> findById(String id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new OrderNotFoundException("Id: " + id)));
    }

    public Flux<Order> findByStatus(String businessId, String status) {
        return orderRepository.findByStatus(businessId, status)
                .switchIfEmpty(Mono.error(new OrderNotFoundException("Status: " + status)));
    }
}
