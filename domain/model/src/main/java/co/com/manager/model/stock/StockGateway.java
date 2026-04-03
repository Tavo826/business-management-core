package co.com.manager.model.stock;

import reactor.core.publisher.Mono;

public interface StockGateway {

    Mono<Stock> getStock(String userMessage);
}
