package dev.codescreen.store.entity;

import dev.codescreen.model.Amount;
import dev.codescreen.model.ResponseCode;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class ProcessedMessage {

    @NonNull
    private LocalDateTime dateTime;

    @NonNull
    private String userId;

    @NonNull
    private String messageId;

    @NonNull
    private Amount transactionAmount;

    private ResponseCode responseCode;

    @NonNull
    private Amount balance;
}
