package com.project.cozystay.payment.controller;

import com.project.cozystay.payment.domain.Payment;
import com.project.cozystay.payment.dto.PaymentResponse;
import com.project.cozystay.payment.service.PaymentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentQueryController {

    private final PaymentQueryService paymentQueryService;

    // 결제 단건 조회
    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long paymentId) {
        Payment payment = paymentQueryService.getById(paymentId);
        return ResponseEntity.ok(toResponse(payment));
    }

    // 예약 기준 결제 조회
    @GetMapping("/bookings/{bookingId}/payment")
    public ResponseEntity<PaymentResponse> getPaymentByBooking(@PathVariable Long bookingId) {
        Payment payment = paymentQueryService.getByBookingId(bookingId);
        return ResponseEntity.ok(toResponse(payment));
    }

    private PaymentResponse toResponse(Payment payment) {
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
