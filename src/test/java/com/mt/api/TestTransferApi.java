package com.mt.api;

import com.mt.TestUtils;
import com.mt.datastore.BankAccounts;
import com.mt.dto.AccountDTO;
import com.mt.dto.TransferDTO;
import com.mt.model.Account;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.math.BigDecimal;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class TestTransferApi extends JerseyTest {

    @Override
    protected Application configure() {
        return new com.mt.Application.MoneyTransferServerResources();
    }

    @Before
    public void beforeEach() {
        TestUtils.clearAccounts();
    }

    @Test
    public void testDeposit_accountNotFound() {
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAccountId("someRandomAccount");
        transferDTO.setAmount(BigDecimal.TEN);

        Response response = target("/deposit").request().put(Entity.entity(transferDTO, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.TEXT_PLAIN, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        assertEquals("No account found with id someRandomAccount", response.readEntity(String.class));
    }

    @Test
    public void testDeposit() {
        Account account = BankAccounts.getInstance().addAccount();
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAccountId(account.getId());
        transferDTO.setAmount(BigDecimal.TEN);

        Response response = target("/deposit").request().put(Entity.entity(transferDTO, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        AccountDTO result = response.readEntity(AccountDTO.class);
        assertEquals(BigDecimal.TEN, result.getBalance());
    }

    @Test
    public void testWithdraw() {
        Account account = new Account(BigDecimal.TEN);
        BankAccounts.getInstance().updateAccount(account);
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAccountId(account.getId());
        transferDTO.setAmount(BigDecimal.ONE);

        Response response = target("/withdraw").request().put(Entity.entity(transferDTO, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        AccountDTO result = response.readEntity(AccountDTO.class);
        assertEquals(BigDecimal.valueOf(9), result.getBalance());
    }

    @Test
    public void testWithdraw_insufficientFunds() {
        Account account = BankAccounts.getInstance().addAccount();
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAccountId(account.getId());
        transferDTO.setAmount(BigDecimal.TEN);

        Response response = target("/withdraw").request().put(Entity.entity(transferDTO, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.TEXT_PLAIN, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        assertEquals("There are insufficient founds in this account for this transaction", response.readEntity(String.class));
    }

    @Test
    public void testTransfer() {
        Account sourceAccount = new Account(BigDecimal.TEN);
        Account destinationAccount = new Account();
        BankAccounts.getInstance().updateAccount(sourceAccount);
        BankAccounts.getInstance().updateAccount(destinationAccount);

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAccountId(sourceAccount.getId());
        transferDTO.setDestinationId(destinationAccount.getId());
        transferDTO.setAmount(BigDecimal.ONE);

        Response response = target("/transfer").request().put(Entity.entity(transferDTO, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        Collection<AccountDTO> accounts = response.readEntity(new GenericType<Collection<AccountDTO>>() {
        });
        assertEquals(2, accounts.size());
        for (AccountDTO accountDTO : accounts) {
            assertEquals(accountDTO.getId().equals(sourceAccount.getId()) ? BigDecimal.valueOf(9) : BigDecimal.ONE, accountDTO.getBalance());
        }
    }

    @Test
    public void testTransfer_insufficientFunds() {
        Account sourceAccount = new Account(BigDecimal.ONE);
        Account destinationAccount = new Account();
        BankAccounts.getInstance().updateAccount(sourceAccount);
        BankAccounts.getInstance().updateAccount(destinationAccount);

        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAccountId(sourceAccount.getId());
        transferDTO.setDestinationId(destinationAccount.getId());
        transferDTO.setAmount(BigDecimal.TEN);

        Response response = target("/transfer").request().put(Entity.entity(transferDTO, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(MediaType.TEXT_PLAIN, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        assertEquals("There are insufficient founds in this account for this transaction", response.readEntity(String.class));
        assertEquals(BigDecimal.ONE, BankAccounts.getInstance().getAccount(sourceAccount.getId()).getBalance());
        assertEquals(BigDecimal.ZERO, BankAccounts.getInstance().getAccount(destinationAccount.getId()).getBalance());
    }
}
