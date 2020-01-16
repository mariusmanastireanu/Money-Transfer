package com.mt.model;

import com.mt.dto.AccountDTO;
import com.mt.exception.InvalidRequest;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {

    private String id;
    private BigDecimal balance;

    public Account() {
        this(BigDecimal.ZERO);
    }

    public Account(final BigDecimal balance) {
        this(generateId(), balance);
    }

    private Account(final String id, final BigDecimal balance) {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRequest("Cannot create account with negative balance");
        }
        this.id = id == null || id.isEmpty() ? generateId() : id;
        this.balance = balance;
    }

    private static String generateId() {
        return UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(final BigDecimal balance) {
        validate(balance);
        this.balance = balance;
    }

    public void deposit(final BigDecimal sumToDeposit) {
        validate(sumToDeposit);
        balance = balance.add(sumToDeposit);
    }

    public void withdraw(final BigDecimal sumToWithdraw) {
        validate(sumToWithdraw);
        BigDecimal result = balance.subtract(sumToWithdraw);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRequest("There are insufficient founds in this account for this transaction");
        }
        balance = result;
    }

    private void validate(final BigDecimal number) {
        if (number == null || number.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidRequest("Amount for this transfer is invalid");
        }
    }

    public static Account fromDTO(final AccountDTO dto) {
        return new Account(dto.getId(), dto.getBalance());
    }

}
