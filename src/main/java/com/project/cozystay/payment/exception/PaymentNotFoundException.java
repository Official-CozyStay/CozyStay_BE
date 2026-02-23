package com.project.cozystay.payment.exception;

// 결제 없음 예외
public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String message){
        super(message);
    }
}
