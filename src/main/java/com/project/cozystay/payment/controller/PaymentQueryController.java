package com.project.cozystay.payment.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.payment.domain.Payment;
import com.project.cozystay.payment.dto.PaymentResponse;
import com.project.cozystay.payment.service.PaymentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal CustomOAuth2User principal) {

        Long requestUserId = principal.getId();

        Payment payment = paymentQueryService.getById(paymentId, requestUserId);
        return ResponseEntity.ok(PaymentResponse.from(payment));
    }

    // 예약 기준 결제 조회
    @GetMapping("/bookings/{bookingId}/payment")
    public ResponseEntity<PaymentResponse> getPaymentByBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomOAuth2User principal) {

        Long requesterUserId = principal.getId();

        Payment payment = paymentQueryService.getByBookingId(bookingId, requesterUserId);
        return ResponseEntity.ok(PaymentResponse.from(payment));
    }


}
