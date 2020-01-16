package com.mt.datastore;

import com.mt.model.Account;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO - in the future replace this with an actual DataBase layer.
public class BankAccounts {

    private static final BankAccounts instance = new BankAccounts();

    private Map<String, Account> accounts = new ConcurrentHashMap<>();

    private BankAccounts() {
        // do nothing.
        // private constructor
    }

    public static BankAccounts getInstance() {
        return instance;
    }

    public Collection<Account> getAllAccounts() {
        return accounts.values();
    }

    public Account getAccount(final String id) {
        return accounts.get(id);
    }

    public Account addAccount() {
        final Account account = new Account();
        accounts.put(account.getId(), account);
        return account;
    }

    public Account updateAccount(final Account account) {
        accounts.put(account.getId(), account);
        return account;
    }

    public Account removeAccount(final String id) {
        return accounts.remove(id);
    }
}
