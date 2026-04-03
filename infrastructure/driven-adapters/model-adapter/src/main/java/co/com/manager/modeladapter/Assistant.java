package co.com.manager.modeladapter;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Assistant {

    String chat(@MemoryId String clientId, @UserMessage String userMessage);
}