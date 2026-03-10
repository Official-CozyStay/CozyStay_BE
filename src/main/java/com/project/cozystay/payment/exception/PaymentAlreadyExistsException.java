package com.project.cozystay.payment.exception;

// 중복 결제 예외
public class PaymentAlreadyExistsException extends RuntimeException {
    public PaymentAlreadyExistsException(String message){
        super(message);
    }
}
