package dev.codescreen.controller;

import dev.codescreen.exception.TransactionError;
import dev.codescreen.exception.TransactionServiceException;
import dev.codescreen.model.AuthorizationRequest;
import dev.codescreen.model.AuthorizationResponse;
import dev.codescreen.model.ResponseCode;
import dev.codescreen.service.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authorization")
public class AuthorizationController {

    @Autowired
    private AuthorizationService authorizationService;

    @PutMapping
    public ResponseEntity<?> authorize(@RequestBody AuthorizationRequest authorizationRequest) {
        try {
            AuthorizationResponse authorizationResponse = authorizationService.removeFunds(authorizationRequest);
            if (authorizationResponse.getResponseCode() == ResponseCode.APPROVED) {
                return ResponseEntity.status(HttpStatus.CREATED).body(authorizationResponse);
            } else {
                return ResponseEntity
                        .status(TransactionError.INSUFFICIENT_FUNDS.getHttpStatus())
                        .body(TransactionError.INSUFFICIENT_FUNDS.getMessage());
            }
        } catch (TransactionServiceException transactionServiceException) {
            return ResponseEntity
                    .status(transactionServiceException.getHttpStatus())
                    .body(transactionServiceException.getTransactionError());
        }
    }
}
