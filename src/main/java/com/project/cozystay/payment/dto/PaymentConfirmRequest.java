package com.project.cozystay.payment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentConfirmRequest {
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
}
