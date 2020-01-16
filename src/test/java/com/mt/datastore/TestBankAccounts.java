package com.mt.datastore;

import com.mt.TestUtils;
import com.mt.model.Account;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestBankAccounts {

    @Before
    public void beforeEach() {
        TestUtils.clearAccounts();
    }

    @Test
    public void testAddAccount() {
        assertEquals(0, BankAccounts.getInstance().getAllAccounts().size());
        BankAccounts.getInstance().addAccount();
        assertEquals(1, BankAccounts.getInstance().getAllAccounts().size());
    }

    @Test
    public void testGetAccount() {
        Account account = new Account();
        String id = account.getId();
        BankAccounts.getInstance().updateAccount(account);
        Account accountFromDatastore = BankAccounts.getInstance().getAccount(id);
        assertEquals(account, accountFromDatastore);
    }

    @Test
    public void testGetAllAccounts() {
        Account account1 = new Account();
        Account account2 = new Account();
        BankAccounts.getInstance().updateAccount(account1);
        BankAccounts.getInstance().updateAccount(account2);

        assertEquals(2, BankAccounts.getInstance().getAllAccounts().size());
        assertTrue(BankAccounts.getInstance().getAllAccounts().contains(account1));
        assertTrue(BankAccounts.getInstance().getAllAccounts().contains(account2));
    }

    @Test
    public void testRemoveAccount() {
        Account account = new Account();
        BankAccounts.getInstance().updateAccount(account);
        assertEquals(1, BankAccounts.getInstance().getAllAccounts().size());
        Account result = BankAccounts.getInstance().removeAccount(account.getId());
        assertEquals(0, BankAccounts.getInstance().getAllAccounts().size());
        assertNotNull(result);
        assertEquals(account.getId(), result.getId());
    }

}

