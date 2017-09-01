package com.talanlabs.mongeez.exception;

public class MongeezException extends RuntimeException {

    public MongeezException(String message) {
        super(message);
    }

    public MongeezException(String message, Throwable cause) {
        super(message, cause);
    }

    public MongeezException(Throwable cause) {
        super(cause);
    }
}
