package dev.codescreen.controller;

import dev.codescreen.exception.TransactionServiceException;
import dev.codescreen.model.LoadRequest;
import dev.codescreen.model.LoadResponse;
import dev.codescreen.service.LoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/load")
public class LoadController {

    @Autowired
    private LoadService loadService;

    @PutMapping
    public ResponseEntity<?> load(@RequestBody LoadRequest loadRequest) {
        try {
            LoadResponse loadResponse = loadService.addFunds(loadRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(loadResponse);
        } catch (TransactionServiceException transactionServiceException) {
            return ResponseEntity
                    .status(transactionServiceException.getHttpStatus())
                    .body(transactionServiceException.getTransactionError());
        }
    }
}
