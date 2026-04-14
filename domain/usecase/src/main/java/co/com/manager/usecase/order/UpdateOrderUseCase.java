package co.com.manager.usecase.order;

import co.com.manager.model.exceptions.OrderNotFoundException;
import co.com.manager.model.order.Order;
import co.com.manager.model.order.OrderRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@AllArgsConstructor
public class UpdateOrderUseCase {

    private final OrderRepository orderRepository;

    public Mono<Order> updateStatus(String id, String status) {

        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new OrderNotFoundException(id)))
                .map(actualOder -> {
                    actualOder.setStatus(status);
                    actualOder.setUpdatedAt(LocalDateTime.now());

                    return actualOder;
                })
                .flatMap(orderRepository::updateStatus);
    }
}
