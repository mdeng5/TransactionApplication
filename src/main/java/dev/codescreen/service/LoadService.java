package dev.codescreen.service;

import dev.codescreen.model.*;
import dev.codescreen.store.ProcessedMessageStore;
import dev.codescreen.store.UnprocessedMessageQueue;
import dev.codescreen.store.entity.ProcessedMessage;
import dev.codescreen.store.entity.UnprocessedMessage;
import org.springframework.stereotype.Service;

@Service
public class LoadService {

    private UnprocessedMessageQueue unprocessedMessageQueue;
    private ProcessedMessageStore processedMessageStore;

    private final int CHECK_FOR_PROCESSED_TIME = 500;

    public LoadService(UnprocessedMessageQueue unprocessedMessageQueue, ProcessedMessageStore processedMessageStore) {
        this.unprocessedMessageQueue = unprocessedMessageQueue;
        this.processedMessageStore = processedMessageStore;
    }

    public LoadResponse addFunds(LoadRequest request) {
        UnprocessedMessage unprocessedMessage = new UnprocessedMessage(
                request.getUserId(),
                request.getMessageId(),
                request.getTransactionAmount()
        );
        unprocessedMessageQueue.enqueue(unprocessedMessage);

        while (processedMessageStore.getResultForMessage(request.getMessageId()) == null) {
            try {
                Thread.sleep(CHECK_FOR_PROCESSED_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        ProcessedMessage result = processedMessageStore.getResultForMessage(request.getMessageId());
        LoadResponse response = new LoadResponse(
                result.getUserId(),
                result.getMessageId(),
                result.getBalance()
        );
        return response;
    }
}
