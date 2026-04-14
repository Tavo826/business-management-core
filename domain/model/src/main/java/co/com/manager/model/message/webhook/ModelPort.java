package co.com.manager.model.message.webhook;

import co.com.manager.model.business.Business;
import reactor.core.publisher.Mono;

public interface ModelPort {

    Mono<String> chat(String userMessage, String clientId, Business business);
}
