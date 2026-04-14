package co.com.manager.modeladapter;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessContext;
import co.com.manager.model.message.webhook.ModelPort;
import co.com.manager.modeladapter.config.ChatMemoryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
public class GeminiModelAdapter implements ModelPort {

    private final Assistant assistant;
    private final ChatMemoryRegistry chatMemoryRegistry;

    public GeminiModelAdapter(Assistant assistant, ChatMemoryRegistry chatMemoryRegistry) {
        this.assistant = assistant;
        this.chatMemoryRegistry = chatMemoryRegistry;
    }

    @Override
    public Mono<String> chat(String userMessage, String clientId, Business business) {
        return Mono.fromCallable(() -> {
            BusinessContext.set(business);
            try {
                return assistant.chat(clientId, userMessage);
            } finally {
                BusinessContext.clear();
            }
        })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(e -> {
                    log.error("Error al comunicarse con el modelo: {}", e.getMessage(), e);
                    chatMemoryRegistry.clear(clientId);
                    log.warn("Chat memory cleared for clientId {} due to model error", clientId);
                });
    }
}
