package dev.codescreen.store.entity;

import dev.codescreen.model.Amount;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class UnprocessedMessage {
    @NonNull
    private String userId;

    @NonNull
    private String messageId;

    @NonNull
    private Amount transactionAmount;
}
