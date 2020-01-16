package com.mt.dto;

import java.math.BigDecimal;

public class TransferDTO {

    private String accountId;
    private String destinationId;
    private BigDecimal amount;

    public String getAccountId() {
        return accountId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
