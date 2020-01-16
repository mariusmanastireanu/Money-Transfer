package com.mt.service;

import com.mt.TestUtils;
import com.mt.datastore.BankAccounts;
import com.mt.dto.AccountDTO;
import com.mt.dto.TransferDTO;
import com.mt.exception.InvalidRequest;
import com.mt.model.Account;
import com.mt.model.TransferType;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

public class TestTransferService {

    private Account accountA = new Account(BigDecimal.TEN);
    private Account accountB = new Account(BigDecimal.ZERO);

    @Before
    public void beforeEach() {
        TestUtils.clearAccounts();
        BankAccounts.getInstance().updateAccount(accountA);
        BankAccounts.getInstance().updateAccount(accountB);
    }

    @Test(expected = InvalidRequest.class)
    public void testValidate_null() {
        TransferService.executeTransfer(null, null);
    }

    @Test(expected = InvalidRequest.class)
    public void testValidate_nullAccountId() {
        TransferService.executeTransfer(createTransfer(null, null, null), null);
    }

    @Test(expected = InvalidRequest.class)
    public void testValidate_nullAmount() {
        TransferService.executeTransfer(createTransfer("id", null, null), null);
    }

    @Test(expected = InvalidRequest.class)
    public void testValidate_nullDestinationId() {
        TransferService.executeTransfer(createTransfer("id", null, BigDecimal.TEN), TransferType.TRANSFER);
    }

    @Test (expected = InvalidRequest.class)
    public void testOperationWithNoAccountFound() {
        TransferService.executeTransfer(createTransfer("id", null, BigDecimal.TEN), TransferType.DEPOSIT);
    }

    @Test (expected = InvalidRequest.class)
    public void testOperationWithNegativeAmount() {
        Account account = BankAccounts.getInstance().addAccount();
        TransferService.executeTransfer(createTransfer(account.getId(), null, BigDecimal.valueOf(-1)), TransferType.DEPOSIT);
    }

    @Test
    public void testDeposit() {
        AccountDTO result = (AccountDTO) TransferService.executeTransfer(
                createTransfer(accountA.getId(), null, BigDecimal.TEN),
                TransferType.DEPOSIT);
        assertEquals(accountA.getId(), result.getId());
        assertEquals(BigDecimal.valueOf(20), result.getBalance());
    }

    @Test
    public void testWithdraw() {
        AccountDTO result = (AccountDTO) TransferService.executeTransfer(
                createTransfer(accountA.getId(), null, BigDecimal.TEN),
                TransferType.WITHDRAW);
        assertEquals(accountA.getId(), result.getId());
        assertEquals(BigDecimal.ZERO, result.getBalance());
    }

    @Test
    public void testTransfer() {
        Collection<AccountDTO> result = (Collection<AccountDTO>) TransferService.executeTransfer(
                createTransfer(accountA.getId(), accountB.getId(), BigDecimal.TEN),
                TransferType.TRANSFER);
        assertEquals(2, result.size());

        Iterator<AccountDTO> iterator = result.iterator();
        AccountDTO resultingSourceAccount = iterator.next();
        assertEquals(accountA.getId(), resultingSourceAccount.getId());
        assertEquals(BigDecimal.ZERO, resultingSourceAccount.getBalance());

        AccountDTO resultingDestinationAccount = iterator.next();
        assertEquals(accountB.getId(), resultingDestinationAccount.getId());
        assertEquals(BigDecimal.TEN, resultingDestinationAccount.getBalance());
    }

    @Test(expected = InvalidRequest.class)
    public void testTransfer_insufficientAmount() {
        TransferService.executeTransfer(
                createTransfer(accountA.getId(), accountB.getId(), BigDecimal.valueOf(20)),
                TransferType.TRANSFER);
    }

    @Test
    public void testTransfer_checkBalance_insufficientAmount() {
        try {
            TransferService.executeTransfer(
                    createTransfer(accountA.getId(), accountB.getId(), BigDecimal.valueOf(20)),
                    TransferType.TRANSFER);
        } catch (InvalidRequest e) {
            // we expect this...
        }
        assertEquals(BigDecimal.TEN, BankAccounts.getInstance().getAccount(accountA.getId()).getBalance());
        assertEquals(BigDecimal.ZERO, BankAccounts.getInstance().getAccount(accountB.getId()).getBalance());
    }

    /**
     * This method will simulate two threads trying to transfer simultaneously an amount of money between the same accounts
     */
    @Test
    public void testTwoThreadsAtTheSameTime() throws BrokenBarrierException, InterruptedException {
        BankAccounts.getInstance().getAccount(accountA.getId()).setBalance(BigDecimal.TEN);
        BankAccounts.getInstance().getAccount(accountB.getId()).setBalance(BigDecimal.TEN);

        CyclicBarrier barrier = new CyclicBarrier(3);
        new Thread(() -> {
            try {
                barrier.await();
                TransferService.executeTransfer(
                        createTransfer(accountA.getId(), accountB.getId(), BigDecimal.valueOf(5)),
                        TransferType.TRANSFER);
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                barrier.await();
                TransferService.executeTransfer(
                        createTransfer(accountB.getId(), accountA.getId(), BigDecimal.valueOf(3)),
                        TransferType.TRANSFER);
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }).start();

        // when all three threads will call await the two transfers will be executed in parallel
        barrier.await();

        // when all three threads will call await again, the transfers will be finished..
        barrier.await();
        assertEquals(BigDecimal.valueOf(8), BankAccounts.getInstance().getAccount(accountA.getId()).getBalance());
        assertEquals(BigDecimal.valueOf(12), BankAccounts.getInstance().getAccount(accountB.getId()).getBalance());
    }

    /**
     * This method will simulate 200 transfers between the exact same two accounts that are run in parallel.
     */
    @Test
    public void testMultipleThreadsTransferring() throws InterruptedException {
        BankAccounts.getInstance().getAccount(accountA.getId()).setBalance(BigDecimal.valueOf(100));
        BankAccounts.getInstance().getAccount(accountB.getId()).setBalance(BigDecimal.valueOf(100));

        // use the countdown latch in order to know when all threads finished to perform the test checks
        CountDownLatch latch = new CountDownLatch(200);

        // create an executor service that will accept 200 tasks to be run in parallel
        ExecutorService executor = Executors.newCachedThreadPool();
        Runnable r1 = () -> {
            try {
                TransferService.executeTransfer(
                        createTransfer(accountA.getId(), accountB.getId(), BigDecimal.valueOf(1)),
                        TransferType.TRANSFER);
            } finally {
                latch.countDown();
            }
        };
        Runnable r2 = () -> {
            try {
                TransferService.executeTransfer(
                        createTransfer(accountB.getId(), accountA.getId(), BigDecimal.valueOf(0.5)),
                        TransferType.TRANSFER);
            } finally {
                latch.countDown();
            }
        };
        for (int i = 0; i < 100; i++) {
            executor.submit(r1);
            executor.submit(r2);
        }
        executor.shutdown();
        latch.await();
        assertEquals(BigDecimal.valueOf(50d), BankAccounts.getInstance().getAccount(accountA.getId()).getBalance());
        assertEquals(BigDecimal.valueOf(150d), BankAccounts.getInstance().getAccount(accountB.getId()).getBalance());
    }

    private static TransferDTO createTransfer(final String srcId, final String destId, final BigDecimal amount) {
        TransferDTO transfer = new TransferDTO();
        transfer.setAccountId(srcId);
        transfer.setDestinationId(destId);
        transfer.setAmount(amount);
        return transfer;
    }
}
