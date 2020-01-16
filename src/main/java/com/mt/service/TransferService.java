package com.mt.service;

import com.mt.datastore.BankAccounts;
import com.mt.dto.AccountDTO;
import com.mt.dto.TransferDTO;
import com.mt.exception.InvalidRequest;
import com.mt.model.Account;
import com.mt.model.TransferType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiConsumer;

public class TransferService {

    private TransferService() {
        // avoid instantiation
    }

    public static Object executeTransfer(final TransferDTO transfer, final TransferType transferType) {
        validateInputParams(transfer, transferType);
        switch (transferType) {
            case DEPOSIT:
                return performAccountOperation(transfer, Account::deposit);
            case WITHDRAW:
                return performAccountOperation(transfer, Account::withdraw);
            case TRANSFER:
                return transferMoney(transfer);
            default:
                throw new UnsupportedOperationException(String.format("Transfer type %s not implemented", transferType));
        }
    }

    private static void validateInputParams(TransferDTO transfer, TransferType transferType) {
        if (transfer == null) {
            throw new InvalidRequest("Transfer data is missing");
        }
        if (transfer.getAccountId() == null) {
            throw new InvalidRequest("Account id is missing");
        }
        if (transfer.getAmount() == null) {
            throw new InvalidRequest("Amount to transfer is missing");
        }
        if (TransferType.TRANSFER.equals(transferType) && transfer.getDestinationId() == null) {
            throw new InvalidRequest("Destination id is missing");
        }
    }

    private static AccountDTO performAccountOperation(final TransferDTO transfer, final BiConsumer<Account, BigDecimal> function) {
        final Account account = getAccount(transfer.getAccountId());
        synchronized (account) {
            function.accept(account, transfer.getAmount());
            return AccountDTO.fromAccount(account);
        }
    }

    private static Collection<AccountDTO> transferMoney(final TransferDTO transfer) {
        final Account sourceAccount = getAccount(transfer.getAccountId());
        final Account destinationAccount = getAccount(transfer.getDestinationId());

        final String sourceId = sourceAccount.getId();
        final String destinationId = destinationAccount.getId();

        // The ids of the accounts should be unique, therefore sorting them in lexicographical order will always provide the same result.
        // We will use them in order to determine the order of the objects to be used as locks in the synchronized blocks bellow.
        // In order to avoid a deadlock situation, we need to make sure that every time we will use the same order.
        final boolean naturalOrder = sourceId.compareTo(destinationId) < 0;
        final Account firstLock = naturalOrder ? sourceAccount : destinationAccount;
        final Account secondLock = naturalOrder ? destinationAccount : sourceAccount;
        synchronized (firstLock) {
            synchronized (secondLock) {
                final BigDecimal amount = transfer.getAmount();
                final BigDecimal initialSourceBalance = sourceAccount.getBalance();
                final BigDecimal initialDestinationBalance = destinationAccount.getBalance();
                try {
                    sourceAccount.withdraw(amount);
                    destinationAccount.deposit(amount);
                    return Arrays.asList(AccountDTO.fromAccount(sourceAccount), AccountDTO.fromAccount(destinationAccount));
                } catch (Exception e) {
                    // for the case when the withdraw operation performed on the source account works well,
                    // but there is something wrong when trying to deposit the money to the destination account
                    sourceAccount.setBalance(initialSourceBalance);
                    destinationAccount.setBalance(initialDestinationBalance);
                    throw e;
                }
            }
        }
    }

    private static Account getAccount(final String id) {
        final Account account = BankAccounts.getInstance().getAccount(id);
        if (account == null) {
            throw new InvalidRequest(String.format("No account found with id %s",id));
        }
        return account;
    }
}
