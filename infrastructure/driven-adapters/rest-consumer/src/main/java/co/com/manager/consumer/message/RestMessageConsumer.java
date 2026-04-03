package co.com.manager.consumer.message;

import co.com.manager.consumer.message.dto.MessageRequest;
import co.com.manager.consumer.message.dto.MessageResponse;
import co.com.manager.model.exceptions.MessageSendException;
import co.com.manager.model.message.user.MessageGateway;
import co.com.manager.model.message.user.UserMessageRequest;
import co.com.manager.model.message.user.UserMessageResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestMessageConsumer implements MessageGateway {

    @Qualifier("messageWebClient")
    private final WebClient client;
    private final MessageMapper mapper;

    @Override
    @CircuitBreaker(name = "messageChat", fallbackMethod = "messageChatFallback")
    public Mono<UserMessageResponse> sendMessage(String phoneNumberId, UserMessageRequest request) {

        log.info("Sending response message to client");

        return client
                .post()
                .uri(uriBuilder -> uriBuilder.path("/{phoneNumberId}/messages").build(phoneNumberId))
                .body(Mono.just(mapper.toRequest(request)), MessageRequest.class)
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
                .bodyToMono(MessageResponse.class)
                .map(mapper::toDomain)
                .onErrorMap(WebClientRequestException.class, e -> {
                    log.error("Network error sending message to phoneNumberId={}: {}", phoneNumberId, e.getMessage());
                    return new MessageSendException("Network error reaching WhatsApp API", e);
                });
    }

    public Mono<UserMessageResponse> messageChatFallback(String phoneNumberId, UserMessageRequest request, Throwable cause) {
        log.error("Circuit breaker activated for phoneNumberId={}, cause={}", phoneNumberId, cause.getMessage());
        return Mono.error(new MessageSendException(
                "Message service unavailable for phoneNumberId=%s".formatted(phoneNumberId), cause));
    }
}
