package dev.codescreen.store.entity;

import dev.codescreen.model.Amount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private String userId;
    private Amount balance;

}
