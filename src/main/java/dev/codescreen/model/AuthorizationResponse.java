package dev.codescreen.model;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationResponse {

    @NonNull
    private String userId;

    @NonNull
    private String messageId;

    private ResponseCode responseCode;

    @NonNull
    private Amount balance;
}
