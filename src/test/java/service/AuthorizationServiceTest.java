package service;

import dev.codescreen.TransactionServiceApplication;
import dev.codescreen.exception.TransactionServiceException;
import dev.codescreen.model.*;
import dev.codescreen.service.AuthorizationService;
import dev.codescreen.store.ProcessedMessageStore;
import dev.codescreen.store.UnprocessedMessageQueue;
import dev.codescreen.store.entity.ProcessedMessage;
import dev.codescreen.store.entity.UnprocessedMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TransactionServiceApplication.class)
class AuthorizationServiceTest {

    @Mock
    private UnprocessedMessageQueue unprocessedMessageQueue;

    @Mock
    private ProcessedMessageStore processedMessageStore;

    @InjectMocks
    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRemoveFundsSuccess() {
        AuthorizationRequest request = new AuthorizationRequest("123", "456", new Amount("100", "USD", DebitCredit.DEBIT));
        doNothing().when(unprocessedMessageQueue).enqueue(any(UnprocessedMessage.class));

        ProcessedMessage processedMessage = new ProcessedMessage(LocalDateTime.now(), "123", "456", new Amount("100", "USD", DebitCredit.DEBIT), ResponseCode.APPROVED, new Amount("90", "USD", DebitCredit.DEBIT));
        when(processedMessageStore.getResultForMessage(request.getMessageId())).thenReturn(processedMessage);

        AuthorizationResponse response = authorizationService.removeFunds(request);

        assertNotNull(response);
        assertEquals("123", response.getUserId());
        assertEquals("456", response.getMessageId());
        assertEquals(ResponseCode.APPROVED, response.getResponseCode());
        assertEquals("90", response.getBalance().getAmount());
        assertEquals("USD", response.getBalance().getCurrency());
        assertEquals(DebitCredit.DEBIT, response.getBalance().getDebitOrCredit());

        verify(unprocessedMessageQueue, times(1)).enqueue(any(UnprocessedMessage.class));
        verify(processedMessageStore, times(2)).getResultForMessage("456");
    }

    @Test
    void testRemoveFundsExceededMaxRetries() {
        AuthorizationRequest request = new AuthorizationRequest("234", "567", new Amount("100", "USD", DebitCredit.DEBIT));
        doNothing().when(unprocessedMessageQueue).enqueue(any(UnprocessedMessage.class));

        when(processedMessageStore.getResultForMessage(request.getMessageId())).thenReturn(null);

        assertThrows(TransactionServiceException.class, () -> authorizationService.removeFunds(request));
        verify(unprocessedMessageQueue, times(1)).enqueue(any(UnprocessedMessage.class));
        verify(processedMessageStore, times(7)).getResultForMessage("567"); //5 retries and the one to exit the loop, and last one to throw the error
    }
}