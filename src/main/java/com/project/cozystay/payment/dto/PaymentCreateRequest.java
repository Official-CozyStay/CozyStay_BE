package com.project.cozystay.payment.dto;

import com.project.cozystay.payment.domain.PaymentMethod;
import lombok.Data;

@Data
public class PaymentCreateRequest {
    private Long bookingId;
    private PaymentMethod paymentMethod;
}
