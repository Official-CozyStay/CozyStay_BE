package com.project.cozystay.payment.external.toss.dto;

import lombok.Getter;

@Getter
public class TossConfirmResponse {
    private String paymentKey;
    private String orderId;
    private String status;
    private String method;
}
