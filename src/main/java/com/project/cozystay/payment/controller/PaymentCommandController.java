package com.project.cozystay.payment.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.payment.domain.Payment;
import com.project.cozystay.payment.domain.PaymentMethod;
import com.project.cozystay.payment.dto.PaymentConfirmRequest;
import com.project.cozystay.payment.dto.PaymentCreateRequest;
import com.project.cozystay.payment.dto.PaymentCreateResponse;
import com.project.cozystay.payment.dto.PaymentResponse;
import com.project.cozystay.payment.service.PaymentCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentCommandController {

    private final PaymentCommandService paymentCommandService;

    // 결제 생성
    @PostMapping
    public ResponseEntity<PaymentCreateResponse> createPayment(
            @RequestBody PaymentCreateRequest request,
            @AuthenticationPrincipal CustomOAuth2User principal
            ){

        Long payerId = principal.getId();
        Long bookingId = request.getBookingId();

        PaymentMethod method = request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.MOCK;

        Payment payment = paymentCommandService.createPayment(bookingId, payerId, method);

        return ResponseEntity.ok(PaymentCreateResponse.from(payment));
    }

    // 결제 성공 처리
    @PostMapping("confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(
            @RequestBody PaymentConfirmRequest request,
            @AuthenticationPrincipal CustomOAuth2User principal
            ){

        Long payerId = principal.getId();

        Payment payment = paymentCommandService.confirmPayment(
                request.getOrderId(),
                payerId,
                request.getPaymentKey(),
                request.getAmount()
        );

        return ResponseEntity.ok(PaymentResponse.from(payment));
    }

    // 결제 실패 처리
    @PostMapping("/{paymentId}/fail")
    public ResponseEntity<PaymentResponse> failPayment(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal CustomOAuth2User principal
    ){
        Long payerId = principal.getId();

        Payment payment = paymentCommandService.failPayment(paymentId, payerId);

        return ResponseEntity.ok(PaymentResponse.from(payment));
    }

}
