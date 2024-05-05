package dev.codescreen.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TransactionServiceException extends RuntimeException{
    private TransactionError transactionError;

    private HttpStatus httpStatus;

    public TransactionServiceException(TransactionError transactionError) {
        this.transactionError = transactionError;
        this.httpStatus = transactionError.getHttpStatus();
    }
}
