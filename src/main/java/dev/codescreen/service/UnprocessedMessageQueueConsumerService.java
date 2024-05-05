package dev.codescreen.service;

import dev.codescreen.exception.TransactionError;
import dev.codescreen.exception.TransactionServiceException;
import dev.codescreen.model.*;
import dev.codescreen.store.ProcessedMessageStore;
import dev.codescreen.store.UnprocessedMessageQueue;
import dev.codescreen.store.UserStore;
import dev.codescreen.store.entity.ProcessedMessage;
import dev.codescreen.store.entity.UnprocessedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Service
@Slf4j
public class UnprocessedMessageQueueConsumerService {

    private final UnprocessedMessageQueue unprocessedMessageQueue;

    private final ProcessedMessageStore processedMessageStore;

    private final UserStore userStore;

    public UnprocessedMessageQueueConsumerService(UnprocessedMessageQueue unprocessedMessageQueue,
                                                  ProcessedMessageStore processedMessageStore,
                                                  UserStore userStore) {
        this.unprocessedMessageQueue = unprocessedMessageQueue;
        this.processedMessageStore = processedMessageStore;
        this.userStore = userStore;
    }

    @PostConstruct
    public void consumeMessages() {
        new Thread(() -> {
            while (true) {
                if (!unprocessedMessageQueue.isEmpty()) {
                    UnprocessedMessage unprocessedMessage = unprocessedMessageQueue.dequeue();

//                    log.info(unprocessedMessage.getMessageId() + " " + unprocessedMessage.getTransactionAmount());

                    Amount balance = userStore.getUser(unprocessedMessage.getUserId()).getBalance();
                    ResponseCode responseCode = null;
                    try {
                        balance = updateFunds(unprocessedMessage);
                        if (unprocessedMessage.getTransactionAmount().getDebitOrCredit() == DebitCredit.DEBIT) {
                            responseCode = ResponseCode.APPROVED;
                        }
                    } catch (TransactionServiceException e) {
                        e.printStackTrace();
                        if (unprocessedMessage.getTransactionAmount().getDebitOrCredit() == DebitCredit.DEBIT) {
                            responseCode = ResponseCode.DENIED;
                        }
                    }

                    ProcessedMessage processedMessage = new ProcessedMessage(
                            LocalDateTime.now(),
                            unprocessedMessage.getUserId(),
                            unprocessedMessage.getMessageId(),
                            unprocessedMessage.getTransactionAmount(),
                            responseCode,
                            balance
                    );

                    processedMessageStore.addProcessedMessage(processedMessage.getMessageId(), processedMessage);
                }
            }
        }).start();
    }

    private Amount updateFunds(UnprocessedMessage unprocessedMessage) {
        if (unprocessedMessage.getTransactionAmount().getDebitOrCredit() == DebitCredit.CREDIT) {
            return userStore.addToBalance(unprocessedMessage.getUserId(), unprocessedMessage.getTransactionAmount());
        } else if (unprocessedMessage.getTransactionAmount().getDebitOrCredit() == DebitCredit.DEBIT) {
            return userStore.subtractFromBalance(unprocessedMessage.getUserId(), unprocessedMessage.getTransactionAmount());
        } else {
            throw new TransactionServiceException(TransactionError.INVALID_TRANSACTION_TYPE);
        }
    }

}
