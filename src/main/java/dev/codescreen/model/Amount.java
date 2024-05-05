package dev.codescreen.model;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class Amount {
    @NonNull
    private String amount;

    @NonNull
    private String currency;

    @NonNull
    private DebitCredit debitOrCredit;
}
