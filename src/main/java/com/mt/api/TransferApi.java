package com.mt.api;

import com.mt.dto.TransferDTO;
import com.mt.service.TransferService;
import com.mt.model.TransferType;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class TransferApi {

    @PUT
    @Path("/deposit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deposit(final TransferDTO transfer) {
        return Response.ok(TransferService.executeTransfer(transfer, TransferType.DEPOSIT)).build();
    }

    @PUT
    @Path("/withdraw")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response withdraw(final TransferDTO transfer) {
        return Response.ok(TransferService.executeTransfer(transfer, TransferType.WITHDRAW)).build();
    }

    @PUT
    @Path("/transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transfer(final TransferDTO transfer) {
        return Response.ok(TransferService.executeTransfer(transfer, TransferType.TRANSFER)).build();
    }

}
