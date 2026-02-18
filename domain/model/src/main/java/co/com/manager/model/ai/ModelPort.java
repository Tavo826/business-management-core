package co.com.manager.model.ai;

import reactor.core.publisher.Mono;

public interface ModelPort {

    Mono<String> chat(String userMessage);
}
