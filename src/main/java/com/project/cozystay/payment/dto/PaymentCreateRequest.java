package com.project.cozystay.payment.dto;

import com.project.cozystay.payment.domain.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCreateRequest {
    private Long bookingId;
    private PaymentMethod paymentMethod;
}
