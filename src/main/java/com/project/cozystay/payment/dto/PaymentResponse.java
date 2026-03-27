package com.project.cozystay.payment.dto;

import com.project.cozystay.payment.domain.Payment;
import com.project.cozystay.payment.domain.PaymentMethod;
import com.project.cozystay.payment.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
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

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getPaymentKey(),
                payment.getPaidAt(),
                payment.getCancelledAt()
        );
    }
}
