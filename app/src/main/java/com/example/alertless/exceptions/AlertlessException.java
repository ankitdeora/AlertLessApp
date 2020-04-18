package com.example.alertless.exceptions;

public class AlertlessException extends Exception {
    public AlertlessException() {
    }

    public AlertlessException(String message) {
        super(message);
    }

    public AlertlessException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlertlessException(Throwable cause) {
        super(cause);
    }
}
