package controller.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codescreen.TransactionServiceApplication;
import dev.codescreen.model.*;
import dev.codescreen.store.ProcessedMessageStore;
import dev.codescreen.store.UserStore;
import dev.codescreen.store.entity.ProcessedMessage;
import dev.codescreen.store.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TransactionServiceApplication.class)
@AutoConfigureMockMvc
public class TransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserStore userStore;

    @Autowired
    private ProcessedMessageStore processedMessageStore;

    private ObjectMapper objectMapper;

    private List<TransactionRecord> recordList;

    @BeforeEach
    public void setUp() throws IOException {
        objectMapper = new ObjectMapper();

        String filePath = "src/test/java/resources/sample_tests";
        recordList = new ArrayList<>();
        boolean isHeader = true;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    TransactionRecord record = new TransactionRecord();
                    record.setAction(Action.fromString(parts[0].trim()));
                    record.setMessageId(parts[1].trim());
                    record.setUserId(parts[2].trim());
                    record.setDebitOrCredit(DebitCredit.valueOf(parts[3].trim()));
                    record.setAmount((parts[4].trim()));
                    record.setResponseCode(ResponseCode.fromString(parts[5].trim()));
                    record.setBalance((parts[6].trim()));
                    recordList.add(record);
                }
            }
        }
    }

    @Test
    public void testMultipleTransactions() throws Exception {
        for (TransactionRecord record : recordList) {
            if (record.getAction() == Action.LOAD) {
                LoadRequest loadRequest = new LoadRequest(
                        record.getUserId(),
                        record.getMessageId(),
                        new Amount(
                                record.getAmount().toString(),
                                "USD",
                                DebitCredit.CREDIT
                        )
                );
                ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                                .put("/load")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loadRequest)));
                if (record.getResponseCode() == ResponseCode.APPROVED) {
                    resultActions.andExpect(status().isCreated());
                } else {
                    resultActions.andExpect(status().isBadRequest());
                }
            }
            if (record.getAction() == Action.AUTHORIZATION) {
                AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                        record.getUserId(),
                        record.getMessageId(),
                        new Amount(
                                record.getAmount().toString(),
                                "USD",
                                DebitCredit.DEBIT
                        )
                );
                ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                                .put("/authorization")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authorizationRequest)));
                if (record.getResponseCode() == ResponseCode.APPROVED) {
                    resultActions.andExpect(status().isCreated());
                } else {
                    resultActions.andExpect(status().isBadRequest());
                }
            }
//            System.out.println(new BigDecimal(record.getBalance().toString()));
//            System.out.println(new BigDecimal(userStore.getUser(record.getUserId()).getBalance().getAmount()));

            assertEquals(record.getBalance(), userStore.getUser(record.getUserId()).getBalance().getAmount());

        }

    }
}