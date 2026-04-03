package co.com.manager.modeladapter;

import co.com.manager.model.message.webhook.ModelPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
public class GeminiModelAdapter implements ModelPort {

    private final Assistant assistant;

    public GeminiModelAdapter(Assistant assistant) {
        this.assistant = assistant;
    }

    @Override
    public Mono<String> chat(String userMessage, String clientId) {
        return Mono.fromCallable(() -> assistant.chat(clientId, userMessage))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(e -> log.error("Error al comunicarse con el modelo: {}", e.getMessage(), e));
    }
}