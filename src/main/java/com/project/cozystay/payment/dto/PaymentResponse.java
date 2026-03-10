package com.project.cozystay.payment.dto;

import com.project.cozystay.payment.domain.PaymentMethod;
import com.project.cozystay.payment.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentResponse {

    private Long paymentId;
    private Long bookingId;

    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    private String paymentKey;

    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;
}
