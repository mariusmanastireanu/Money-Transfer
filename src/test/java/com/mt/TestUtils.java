package com.mt;

import com.mt.datastore.BankAccounts;
import com.mt.model.Account;

import java.lang.reflect.Field;
import java.util.Map;

public class TestUtils {

    private TestUtils() {
        // private constructor
    }

    public static void clearAccounts() {
        try {
            final Field field = BankAccounts.getInstance().getClass().getDeclaredField("accounts");
            field.setAccessible(true);
            ((Map<String, Account>) field.get(BankAccounts.getInstance())).clear();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
