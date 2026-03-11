package com.project.cozystay.payment.dto;

import com.project.cozystay.payment.domain.Payment;
import com.project.cozystay.payment.domain.PaymentMethod;
import com.project.cozystay.payment.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentCreateResponse {
    private Long paymentId;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;

    public static PaymentCreateResponse from(Payment payment) {
        return new PaymentCreateResponse(
                payment.getId(),
                payment.getStatus(),
                payment.getPaymentMethod(),
                payment.getAmount()
        );
    }
}
