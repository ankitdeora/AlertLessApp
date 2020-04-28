package com.example.alertless.exceptions;

public class AlertlessRuntimeException extends RuntimeException {
    public AlertlessRuntimeException() {
    }

    public AlertlessRuntimeException(String message) {
        super(message);
    }

    public AlertlessRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlertlessRuntimeException(Throwable cause) {
        super(cause);
    }

    public AlertlessRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
