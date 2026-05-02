package co.com.manager.modeladapter.config;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.*;
import dev.langchain4j.memory.ChatMemory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SanitizingChatMemory implements ChatMemory {

    private final ChatMemory delegate;

    public SanitizingChatMemory(ChatMemory delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object id() {
        return delegate.id();
    }

    @Override
    public void add(ChatMessage message) {
        delegate.add(message);
    }

    @Override
    public List<ChatMessage> messages() {
        return sanitize(delegate.messages());
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    private List<ChatMessage> sanitize(List<ChatMessage> original) {
        if (original == null || original.isEmpty()) {
            return original;
        }

        Set<String> resultIds = new HashSet<>();
        for (ChatMessage m : original) {
            if (m instanceof ToolExecutionResultMessage r) {
                resultIds.add(r.id());
            }
        }

        List<ChatMessage> filtered = new ArrayList<>(original.size());
        Set<String> expectedResults = new HashSet<>();

        for (ChatMessage m : original) {
            if (m instanceof AiMessage ai && ai.hasToolExecutionRequests()) {
                boolean allMatched = true;
                for (ToolExecutionRequest req : ai.toolExecutionRequests()) {
                    if (!resultIds.contains(req.id())) {
                        allMatched = false;
                        break;
                    }
                }
                if (!allMatched) {
                    continue;
                }
                for (ToolExecutionRequest req : ai.toolExecutionRequests()) {
                    expectedResults.add(req.id());
                }
                filtered.add(ai);
            } else if (m instanceof ToolExecutionResultMessage r) {
                if (expectedResults.remove(r.id())) {
                    filtered.add(r);
                }
            } else {
                filtered.add(m);
            }
        }

        int firstNonSystem = 0;
        while (firstNonSystem < filtered.size() && filtered.get(firstNonSystem) instanceof SystemMessage) {
            firstNonSystem++;
        }
        while (firstNonSystem < filtered.size() && !(filtered.get(firstNonSystem) instanceof UserMessage)) {
            filtered.remove(firstNonSystem);
        }

        return filtered;
    }
}
