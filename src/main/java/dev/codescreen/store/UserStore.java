package dev.codescreen.store;

import dev.codescreen.exception.TransactionServiceException;
import dev.codescreen.model.Amount;
import dev.codescreen.model.DebitCredit;
import dev.codescreen.exception.TransactionError;
import dev.codescreen.store.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserStore {
    private final Map<String, User> userStore = new HashMap<>();

    public synchronized void addUser(String userId, User user) {
        userStore.put(userId, user);
    }

    public synchronized User getUser(String userId) {
        if (!userStore.containsKey(userId)) {
            Amount balance = new Amount(
                    "0.00",
                    "USD",
                    DebitCredit.CREDIT
            );
            User user = new User(
                    userId,
                    balance
            );
            userStore.put(userId, user);
        }
        return userStore.get(userId);
    }

    public synchronized Amount addToBalance(String userId, Amount amount) {
        Amount balance = getUser(userId).getBalance();
        BigDecimal sum = new BigDecimal(balance.getAmount()).add(new BigDecimal(amount.getAmount()));
        balance.setAmount(sum.toString());
        userStore.get(userId).setBalance(balance);
        return balance;
    }

    public synchronized Amount subtractFromBalance(String userId, Amount amount) {
        Amount balance = getUser(userId).getBalance();
        if (new BigDecimal(balance.getAmount()).compareTo(new BigDecimal(amount.getAmount())) == -1) {
            throw new TransactionServiceException(TransactionError.INSUFFICIENT_FUNDS);
        } else {
            BigDecimal diff = new BigDecimal(balance.getAmount()).subtract(new BigDecimal(amount.getAmount()));
            balance.setAmount(diff.toString());
            userStore.get(userId).setBalance(balance);
        }
        return balance;
    }
}