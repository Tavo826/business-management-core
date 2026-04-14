package co.com.manager.modeladapter.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChatMemoryRegistry {

    private final ConcurrentMap<Object, ChatMemory> memories = new ConcurrentHashMap<>();
    private final int maxMessages;

    public ChatMemoryRegistry(int maxMessages) {
        this.maxMessages = maxMessages;
    }

    public ChatMemory get(Object clientId) {
        return memories.computeIfAbsent(clientId,
                id -> new SanitizingChatMemory(MessageWindowChatMemory.withMaxMessages(maxMessages)));
    }

    public void clear(Object clientId) {
        ChatMemory removed = memories.remove(clientId);
        if (removed != null) {
            removed.clear();
        }
    }
}