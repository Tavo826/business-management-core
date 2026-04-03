package co.com.manager.consumer.stock;

import co.com.manager.consumer.stock.dto.StockDto;
import co.com.manager.model.exceptions.StockGetException;
import co.com.manager.model.stock.Stock;
import co.com.manager.model.stock.StockGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestStockConsumer implements StockGateway {

    @Qualifier("stockWebClient")
    private final WebClient client;
    private final StockMapper mapper;

    @Override
    @CircuitBreaker(name = "stockRequest", fallbackMethod = "stockRequestFallback")
    public Mono<Stock> getStock(String userMessage) {

        log.info("Getting stock from info");

        return client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", userMessage)
                        .queryParam("limit", 5)
                        .queryParam("available_only", true)
                        .build())
                .retrieve()
                .bodyToMono(StockDto.class)
                .map(mapper::toDomain)
                .onErrorMap(WebClientRequestException.class, e -> {
                    log.error("Network error getting stock", e.getMessage());
                    return new StockGetException("Network error getting stock", e);
                });
    }
}
