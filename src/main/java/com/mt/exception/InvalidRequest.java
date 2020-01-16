package com.mt.exception;

import javax.ws.rs.core.Response;

// TODO - similar with this create more specific exceptions
public class InvalidRequest extends GenericRuntimeException {

    public InvalidRequest(final String message) {
        super(message);
    }

    @Override
    public Response.Status getResponseStatus() {
        return Response.Status.BAD_REQUEST;
    }

}
