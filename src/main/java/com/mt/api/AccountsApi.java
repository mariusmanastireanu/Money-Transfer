package com.mt.api;

import com.mt.datastore.BankAccounts;
import com.mt.dto.AccountDTO;
import com.mt.model.Account;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.LinkedList;

@Path("/accounts")
public class AccountsApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccounts() {
        final Collection<AccountDTO> result = new LinkedList<>();
        for (final Account account : BankAccounts.getInstance().getAllAccounts()) {
            result.add(AccountDTO.fromAccount(account));
        }
        return Response.ok(result).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam("id") String id) {
        Account account = BankAccounts.getInstance().getAccount(id);
        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(AccountDTO.fromAccount(account)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAccount() {
        return Response.ok(BankAccounts.getInstance().addAccount()).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAccount(final AccountDTO account) {
        return Response.ok(BankAccounts.getInstance().updateAccount(Account.fromDTO(account))).build();
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeAccount(@PathParam("id") String id) {
        Account account = BankAccounts.getInstance().removeAccount(id);
        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(AccountDTO.fromAccount(account)).build();
    }

}
