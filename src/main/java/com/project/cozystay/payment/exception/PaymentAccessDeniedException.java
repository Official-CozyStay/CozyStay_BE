package com.project.cozystay.payment.exception;

public class PaymentAccessDeniedException extends RuntimeException{
    public PaymentAccessDeniedException(String message){
        super(message);
    }
}
