package dev.codescreen.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TransactionError {
    INVALID_TRANSACTION_TYPE("Invalid Transaction type", HttpStatus.BAD_REQUEST),
    TRANSACTION_ERROR("Transaction error", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_FUNDS("Insufficient funds", HttpStatus.BAD_REQUEST),
    EXCEEDED_MAX_RETRIES("Exceeded max retry attempts. Please wait X minutes, your transaction may be processed in a bit", HttpStatus.INTERNAL_SERVER_ERROR);
    private String message;
    private HttpStatus httpStatus;

}
