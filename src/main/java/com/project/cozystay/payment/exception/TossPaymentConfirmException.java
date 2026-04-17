package com.project.cozystay.payment.exception;

public class TossPaymentConfirmException extends RuntimeException {
    public TossPaymentConfirmException(String message) {
        super(message);
    }

    public TossPaymentConfirmException(String message, Throwable cause) {
        super(message, cause);
    }
}
