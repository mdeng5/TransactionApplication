package controller.integrationtest;

import dev.codescreen.model.DebitCredit;
import dev.codescreen.model.ResponseCode;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecord {
    private Action action;
    private String messageId;
    private String userId;
    private String amount;
    private DebitCredit debitOrCredit;
    private ResponseCode responseCode;
    private String balance;

}