package co.com.manager.model.stock;

import co.com.manager.model.order.OrderItem;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockGateway {

    Mono<Stock> getStock(String userMessage);
    Mono<Void> updateStock(List<OrderItem> orderItem);
}
