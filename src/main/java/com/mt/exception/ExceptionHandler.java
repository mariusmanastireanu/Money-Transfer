package com.mt.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<GenericRuntimeException> {

    @Override
    public Response toResponse(GenericRuntimeException exception) {
        return Response
                .status(exception.getResponseStatus())
                .entity(exception.getMessage())
                .type("text/plain")
                .build();
    }
}
