package com.example.alertless.exceptions;

public class AlertlessIllegalArgumentException extends AlertlessException {

    public AlertlessIllegalArgumentException() {
    }

    public AlertlessIllegalArgumentException(String message) {
        super(message);
    }

    public AlertlessIllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlertlessIllegalArgumentException(Throwable cause) {
        super(cause);
    }
}
