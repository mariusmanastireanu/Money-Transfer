package com.mt.exception;

import javax.ws.rs.core.Response;

public abstract class GenericRuntimeException extends RuntimeException {

    public GenericRuntimeException(){
        super();
    }

    public GenericRuntimeException(final String message) {
        super(message);
    }

    public GenericRuntimeException(final Throwable throwable) {
        super(throwable);
    }

    public GenericRuntimeException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public abstract Response.Status getResponseStatus();
}
