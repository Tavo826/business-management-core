package co.com.manager.usecase.order;

import co.com.manager.model.exceptions.OrderNotFoundException;
import co.com.manager.model.order.Order;
import co.com.manager.model.order.OrderRepository;
import co.com.manager.model.order.Status;
import co.com.manager.model.stock.StockGateway;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@AllArgsConstructor
public class UpdateOrderUseCase {

    private final OrderRepository orderRepository;
    private final StockGateway stockGateway;

    public Mono<Order> update(String id, Order orderRequest) {

        return orderRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new OrderNotFoundException(id))))
                .map(actualOder -> {
                    actualOder.setCustomerName(orderRequest.getCustomerName());
                    actualOder.setCustomerPhone(orderRequest.getCustomerPhone());
                    actualOder.setCustomerAddress(orderRequest.getCustomerAddress());
                    actualOder.setStatus(orderRequest.getStatus());
                    actualOder.setUpdatedAt(LocalDateTime.now());

                    return actualOder;
                })
                .flatMap(orderRepository::updateOrder)
                .flatMap(order -> {
                    if (Status.CONFIRMED.name().equals(order.getStatus())) {
                        return stockGateway.updateStock(order.getItems()).thenReturn(order);
                    }
                    return Mono.just(order);
                });
    }
}
