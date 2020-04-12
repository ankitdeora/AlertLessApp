package com.example.alertless.exceptions;

public class AlertlessDatabaseException extends Exception {
    public AlertlessDatabaseException() {
    }

    public AlertlessDatabaseException(String message) {
        super(message);
    }

    public AlertlessDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlertlessDatabaseException(Throwable cause) {
        super(cause);
    }
}
