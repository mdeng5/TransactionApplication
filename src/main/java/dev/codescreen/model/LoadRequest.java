package dev.codescreen.model;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class LoadRequest {

    @NonNull
    private String userId;

    @NonNull
    private String messageId;

    @NonNull
    private Amount transactionAmount;
}
