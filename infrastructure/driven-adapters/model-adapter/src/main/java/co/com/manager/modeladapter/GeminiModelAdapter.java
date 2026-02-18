package co.com.manager.modeladapter;

import co.com.manager.model.ai.ModelPort;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class GeminiModelAdapter implements ModelPort {

    private final ChatModel chatModel;
    private final String systemPrompt;

    public GeminiModelAdapter(
            ChatModel chatModel,
            @Value("classpath:prompts/system-prompt.txt") Resource systemPromptResource) throws IOException {
        this.chatModel = chatModel;
        this.systemPrompt = new String(systemPromptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    @Override
    public Mono<String> chat(String userMessage) {
        return Mono.fromCallable(() -> processChat(userMessage))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private String processChat(String userMessage) {
        ChatResponse response = chatModel.chat(
                SystemMessage.from(systemPrompt),
                UserMessage.from(userMessage)
        );
        return response.aiMessage().text();
    }
}