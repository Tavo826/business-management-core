package co.com.manager.modeladapter.config;

import co.com.manager.model.business.Business;
import co.com.manager.model.business.BusinessContext;
import co.com.manager.model.message.user.MessageGateway;
import co.com.manager.model.order.OrderRepository;
import co.com.manager.model.stock.StockGateway;
import co.com.manager.modeladapter.Assistant;
import co.com.manager.modeladapter.tool.OrderTool;
import co.com.manager.modeladapter.tool.StockTool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

    @Bean
    public StockTool stockTool(StockGateway stockGateway) {
        return new StockTool(stockGateway);
    }

    @Bean
    public OrderTool orderTool(OrderRepository orderRepository, MessageGateway messageGateway) {
        return new OrderTool(orderRepository, messageGateway);
    }

    @Bean
    public Assistant assistant(
            ChatModel chatModel,
            StockTool stockTool,
            OrderTool orderTool,
            @Value("${adapters.gemini.memory-size}") int memorySize,
            @Value("classpath:prompts/system-prompt.txt") Resource systemPromptResource) throws IOException {

        String systemPrompt = new String(
                systemPromptResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        return AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemoryProvider(clientId ->
                        MessageWindowChatMemory.withMaxMessages(memorySize))
                .systemMessageProvider(clientId -> {
                    Business business = BusinessContext.get();
                    if (business != null && business.getDescription() != null) {
                        return systemPrompt.replace("{business_description}", business.getDescription());
                    }
                    return systemPrompt;
                })
                .tools(stockTool, orderTool)
                .build();
    }
}