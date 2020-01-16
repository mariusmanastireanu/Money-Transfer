package com.mt.model;

import com.mt.dto.AccountDTO;
import com.mt.exception.InvalidRequest;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestAccount {

    @Test
    public void testConstructor_default() {
        Account account = new Account();
        assertNotNull(account.getId());
        assertNotNull(account.getBalance());
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    public void testConstructor_balance() {
        Account account = new Account(BigDecimal.TEN);
        assertNotNull(account.getId());
        assertNotNull(account.getBalance());
        assertEquals(BigDecimal.TEN, account.getBalance());
    }

    @Test(expected = InvalidRequest.class)
    public void testConstructor_balance_null() {
        new Account(null);
    }

    @Test(expected = InvalidRequest.class)
    public void testConstructor_balance_negative() {
        new Account(BigDecimal.valueOf(-1d));
    }

    @Test
    public void testFromDTO() {
        AccountDTO accountDTO = new AccountDTO("id", BigDecimal.TEN);
        Account account = Account.fromDTO(accountDTO);
        assertEquals(accountDTO.getId(), account.getId());
        assertEquals(accountDTO.getBalance(), account.getBalance());
    }

    @Test
    public void testDeposit() {
        Account account = new Account();
        assertEquals(BigDecimal.ZERO, account.getBalance());
        account.deposit(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, account.getBalance());
    }

    @Test(expected = InvalidRequest.class)
    public void testDeposit_null() {
        Account account = new Account();
        account.deposit(null);
    }

    @Test(expected = InvalidRequest.class)
    public void testDeposit_negative() {
        Account account = new Account();
        account.deposit(BigDecimal.valueOf(-1d));
    }

    @Test
    public void testWithdraw() {
        Account account = new Account(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, account.getBalance());
        account.withdraw(BigDecimal.ONE);
        assertEquals(BigDecimal.valueOf(9), account.getBalance());
    }

    @Test(expected = InvalidRequest.class)
    public void testWithdraw_null() {
        Account account = new Account(BigDecimal.TEN);
        account.withdraw(null);
    }

    @Test(expected = InvalidRequest.class)
    public void testWithdraw_negative() {
        Account account = new Account(BigDecimal.TEN);
        account.withdraw(BigDecimal.valueOf(-1d));
    }

    @Test(expected = InvalidRequest.class)
    public void testWithdraw_insufficientFunds() {
        Account account = new Account(BigDecimal.ONE);
        assertEquals(BigDecimal.ONE, account.getBalance());
        account.withdraw(BigDecimal.TEN);
    }

    @Test
    public void setBalance() {
        Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, account.getBalance());
    }

    @Test(expected = InvalidRequest.class)
    public void setBalance_null() {
        Account account = new Account();
        account.setBalance(null);
    }

    @Test(expected = InvalidRequest.class)
    public void setBalance_negative() {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(-1d));
    }

}

