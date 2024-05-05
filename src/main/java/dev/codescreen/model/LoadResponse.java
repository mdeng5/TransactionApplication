package dev.codescreen.model;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class LoadResponse {

    @NonNull
    private String userId;

    @NonNull
    private String messageId;

    @NonNull
    private Amount balance;
}
