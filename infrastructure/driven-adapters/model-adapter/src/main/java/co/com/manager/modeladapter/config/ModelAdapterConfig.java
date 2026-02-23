package co.com.manager.modeladapter.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelAdapterConfig {

    @Bean
    public ChatModel chatModel(
            @Value("${adapters.gemini.api-key}") String apiKey,
            @Value("${adapters.gemini.model-name:gemini-2.0-flash}") String modelName,
            @Value("${adapters.gemini.temperature:0.7}") double temperature) {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .build();
    }
}
