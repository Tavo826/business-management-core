package co.com.manager.model.message.webhook;

import reactor.core.publisher.Mono;

public interface ModelPort {

    Mono<String> chat(String userMessage);
}
