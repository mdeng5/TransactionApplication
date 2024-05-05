package dev.codescreen.service;

import dev.codescreen.exception.TransactionError;
import dev.codescreen.exception.TransactionServiceException;
import dev.codescreen.model.*;
import dev.codescreen.store.ProcessedMessageStore;
import dev.codescreen.store.UnprocessedMessageQueue;
import dev.codescreen.store.entity.ProcessedMessage;
import dev.codescreen.store.entity.UnprocessedMessage;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private UnprocessedMessageQueue unprocessedMessageQueue;

    private ProcessedMessageStore processedMessageStore;
    private final int CHECK_FOR_PROCESSED_TIME = 500;
    private final int NUMBER_OF_RETRIES = 5;

    public AuthorizationService(UnprocessedMessageQueue unprocessedMessageQueue, ProcessedMessageStore processedMessageStore) {
        this.unprocessedMessageQueue = unprocessedMessageQueue;
        this.processedMessageStore = processedMessageStore;
    }
    public AuthorizationResponse removeFunds(AuthorizationRequest request) {
        UnprocessedMessage unprocessedMessage = new UnprocessedMessage(
                request.getUserId(),
                request.getMessageId(),
                request.getTransactionAmount()
        );
        unprocessedMessageQueue.enqueue(unprocessedMessage);
//        System.out.println(unprocessedMessage);

        int retryAttempts = 0;
        while (processedMessageStore.getResultForMessage(request.getMessageId()) == null && retryAttempts < NUMBER_OF_RETRIES) {
            try {
                Thread.sleep(CHECK_FOR_PROCESSED_TIME);
                retryAttempts++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (retryAttempts >= NUMBER_OF_RETRIES && processedMessageStore.getResultForMessage(request.getMessageId()) == null) {
            throw new TransactionServiceException(TransactionError.EXCEEDED_MAX_RETRIES);
        }

        ProcessedMessage result = processedMessageStore.getResultForMessage(request.getMessageId());
        AuthorizationResponse response = new AuthorizationResponse(
                result.getUserId(),
                result.getMessageId(),
                result.getResponseCode(),
                result.getBalance()
        );
        return response;
    }
}
