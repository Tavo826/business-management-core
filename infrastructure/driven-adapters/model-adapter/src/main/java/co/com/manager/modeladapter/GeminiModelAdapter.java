package co.com.manager.modeladapter;

import co.com.manager.model.message.webhook.ModelPort;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class GeminiModelAdapter implements ModelPort {

    private final ChatModel chatModel;
    private final String systemPrompt;
    private final String memorySize;
    private final ConcurrentHashMap<String, ChatMemory> clientMemories = new ConcurrentHashMap<>();

    public GeminiModelAdapter(
            ChatModel chatModel,
            @Value("${adapters.gemini.memory-size}") String memorySize,
            @Value("classpath:prompts/system-prompt.txt") Resource systemPromptResource) throws IOException {
        this.chatModel = chatModel;
        this.memorySize = memorySize;
        this.systemPrompt = new String(systemPromptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    @Override
    public Mono<String> chat(String userMessage, String clientId) {
        return Mono.fromCallable(() -> processChat(userMessage, clientId))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(e -> log.error("Error al comunicarse con el modelo: {}", e.getMessage(), e));
    }

    private String processChat(String userMessage, String clientId) {
        ChatMemory memory = clientMemories.computeIfAbsent(clientId, id -> {
            ChatMemory newMemory = MessageWindowChatMemory.withMaxMessages(Integer.parseInt(memorySize));
            newMemory.add(SystemMessage.from(systemPrompt));
            return newMemory;
        });

        memory.add(UserMessage.from(userMessage));

        ChatResponse response = chatModel.chat(memory.messages());
        String aiResponse = response.aiMessage().text();

        memory.add(response.aiMessage());

        log.debug("Chat memory size for client {}: {}", clientId, memory.messages().size());

        return aiResponse;
    }
}