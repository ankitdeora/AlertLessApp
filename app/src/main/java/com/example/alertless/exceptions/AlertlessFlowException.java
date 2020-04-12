package com.example.alertless.exceptions;

public class AlertlessFlowException extends Exception {
    public AlertlessFlowException() {
    }

    public AlertlessFlowException(String message) {
        super(message);
    }

    public AlertlessFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlertlessFlowException(Throwable cause) {
        super(cause);
    }
}
