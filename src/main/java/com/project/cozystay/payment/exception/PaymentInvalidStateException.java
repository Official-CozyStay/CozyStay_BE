package com.project.cozystay.payment.exception;

// 상태 예외
public class PaymentInvalidStateException extends RuntimeException{
    public PaymentInvalidStateException(String message){
        super(message);
    }
}
