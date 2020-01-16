package com.mt.api;

import com.mt.TestUtils;
import com.mt.datastore.BankAccounts;
import com.mt.dto.AccountDTO;
import com.mt.model.Account;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.math.BigDecimal;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class TestAccountsApi extends JerseyTest {

    @Override
    protected Application configure() {
        return new com.mt.Application.MoneyTransferServerResources();
    }

    @Before
    public void beforeEach() {
        TestUtils.clearAccounts();
    }

    @Test
    public void testGetAll() {
        BankAccounts.getInstance().addAccount();
        BankAccounts.getInstance().addAccount();
        BankAccounts.getInstance().addAccount();

        Response response = target("/accounts").request().get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        Collection<AccountDTO> accounts = response.readEntity(new GenericType<Collection<AccountDTO>>() {});
        assertEquals(3, accounts.size());
    }

    @Test
    public void testGet_ok() {
        Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        BankAccounts.getInstance().updateAccount(account);

        Response response = target("/accounts/" + account.getId()).request().get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        AccountDTO accountDTO = response.readEntity(AccountDTO.class);
        assertEquals(account.getId(), accountDTO.getId());
        assertEquals(account.getBalance(), accountDTO.getBalance());
    }

    @Test
    public void testGet_notFound() {
        Response response = target("/accounts/someRandomId").request().get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testAddAccount() {
        Response response = target("/accounts").request().post(Entity.json(null));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        AccountDTO accountDTO = response.readEntity(AccountDTO.class);
        assertEquals(BigDecimal.ZERO, accountDTO.getBalance());
    }

    @Test
    public void testUpdateAccount() {
        Account account = new Account();
        account.setBalance(BigDecimal.TEN);
        Response response = target("/accounts/").request().put(Entity.entity(AccountDTO.fromAccount(account), MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        AccountDTO accountDTO = response.readEntity(AccountDTO.class);
        assertEquals(account.getId(), accountDTO.getId());
        assertEquals(BigDecimal.TEN, accountDTO.getBalance());
    }

    @Test
    public void testRemoveAccount_ok() {
        Account account = new Account();
        BankAccounts.getInstance().updateAccount(account);

        Response response = target("/accounts/" + account.getId()).request().method("DELETE");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE));

        AccountDTO accountDTO = response.readEntity(AccountDTO.class);
        assertEquals(account.getId(), accountDTO.getId());
    }

    @Test
    public void testRemoveAccount_notFound() {
        Response response = target("/accounts/someRandomId").request().method("DELETE");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

}

