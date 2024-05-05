package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codescreen.TransactionServiceApplication;
import dev.codescreen.controller.AuthorizationController;
import dev.codescreen.exception.TransactionError;
import dev.codescreen.exception.TransactionServiceException;
import dev.codescreen.model.*;
import dev.codescreen.service.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TransactionServiceApplication.class)
@AutoConfigureMockMvc
public class AuthorizationControllerTest {

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private AuthorizationController authorizationController;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authorizationController).build();
    }

    @Test
    public void testAuthorizeSuccess() throws Exception {
        String userId = "123";
        String messageId = "456";
        Amount transactionAmount = new Amount("100", "USD", DebitCredit.DEBIT);
        AuthorizationRequest request = new AuthorizationRequest(
                userId,
                messageId,
                transactionAmount
        );
        AuthorizationResponse response = new AuthorizationResponse(
                userId,
                messageId,
                ResponseCode.APPROVED,
                transactionAmount
        );
        when(authorizationService.removeFunds(any(AuthorizationRequest.class)))
                .thenReturn(response);
        mockMvc.perform(MockMvcRequestBuilders
                .put("/authorization")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testAuthorizeError() throws Exception {
        String userId = "123";
        String messageId = "456";
        Amount transactionAmount = new Amount("100", "USD", DebitCredit.DEBIT);
        AuthorizationRequest request = new AuthorizationRequest(
                userId,
                messageId,
                transactionAmount
        );
        when(authorizationService.removeFunds(any(AuthorizationRequest.class)))
                .thenThrow(new TransactionServiceException(TransactionError.TRANSACTION_ERROR));
        mockMvc.perform(MockMvcRequestBuilders
                .put("/authorization")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
