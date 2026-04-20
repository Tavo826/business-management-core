package co.com.manager.consumer.stock;

import co.com.manager.consumer.stock.dto.StockDto;
import co.com.manager.model.exceptions.MessageSendException;
import co.com.manager.model.exceptions.StockGetException;
import co.com.manager.model.order.OrderItem;
import co.com.manager.model.stock.Stock;
import co.com.manager.model.stock.StockGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestStockConsumer implements StockGateway {

    @Qualifier("stockWebClient")
    private final WebClient client;

    @Override
    @CircuitBreaker(name = "getStockRequest", fallbackMethod = "getStockRequestFallback")
    public Mono<Stock> getStock(String userMessage) {

        log.info("Getting stock info");

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
                .map(StockMapper::toDomain)
                .onErrorMap(WebClientRequestException.class, e -> {
                    log.error("Network error getting stock", e.getMessage());
                    return new StockGetException("Network error getting stock", e);
                });
    }

    @Override
    @CircuitBreaker(name = "updateStockRequest", fallbackMethod = "updateStockRequestFallback")
    public Mono<Void> updateStock(List<OrderItem> orderItems) {

        log.info("Updating stock");

        System.out.println("orderItems: " + orderItems);

        return client
                .post()
                .uri(uriBuilder -> uriBuilder.path("/stock/update").build())
                .bodyValue(StockMapper.toRequestUpdate(orderItems))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .doOnNext(body -> log.error("WhatsApp API client error [{}]: {}", response.statusCode(), body))
                                .map(body -> new MessageSendException("WhatsApp API rejected the request [%s]: %s"
                                        .formatted(response.statusCode(), body))))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .doOnNext(body -> log.error("WhatsApp API server error [{}]: {}", response.statusCode(), body))
                                .map(body -> new MessageSendException("WhatsApp API internal error [%s]: %s"
                                        .formatted(response.statusCode(), body))))
                .bodyToMono(Void.class)
                .onErrorMap(WebClientRequestException.class, e -> {
                    log.error("Network error updating stock", e.getMessage());
                    return new StockGetException("Network error getting stock", e);
                });
    }

    public Mono<Stock> getStockRequestFallback(String userMessage, Throwable cause) {
        log.error("Circuit breaking activated for cause={}", cause.getMessage());
        return Mono.error(new StockGetException(
                "Stock service unavailable", cause));
    }

    public Mono<Stock> updateStockRequestFallback(Throwable cause) {
        log.error("Circuit breaking activated for cause={}", cause.getMessage());
        return Mono.error(new StockGetException(
                "Stock service unavailable", cause));
    }
}
