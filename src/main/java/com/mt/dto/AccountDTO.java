package com.mt.dto;

import com.mt.model.Account;

import java.math.BigDecimal;

public class AccountDTO {

    private final String id;
    private final BigDecimal balance;

    public AccountDTO() {
        this(null, BigDecimal.ZERO);
    }

    public AccountDTO(final String id, final BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public static AccountDTO fromAccount(final Account account) {
        return new AccountDTO(account.getId(), account.getBalance());
    }

}
