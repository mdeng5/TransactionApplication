package dev.codescreen.store;

import dev.codescreen.store.entity.ProcessedMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProcessedMessageStore {
    private final Map<String, ProcessedMessage> processedMessages = new HashMap<>();

    public synchronized void addProcessedMessage(String messageId, ProcessedMessage processedMessage) {
        processedMessages.put(messageId, processedMessage);
    }

    public synchronized ProcessedMessage getResultForMessage(String messageId) {
        return processedMessages.get(messageId);
    }
}
