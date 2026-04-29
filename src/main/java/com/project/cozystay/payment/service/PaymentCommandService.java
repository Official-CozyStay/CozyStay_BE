package com.project.cozystay.payment.service;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.domain.BookingStatus;
import com.project.cozystay.booking.exception.BookingNotFoundException;
import com.project.cozystay.booking.repository.BookingRepository;
import com.project.cozystay.payment.domain.Payment;
import com.project.cozystay.payment.domain.PaymentMethod;
import com.project.cozystay.payment.domain.PaymentStatus;
import com.project.cozystay.payment.exception.PaymentAlreadyExistsException;
import com.project.cozystay.payment.exception.PaymentInvalidStateException;
import com.project.cozystay.payment.exception.PaymentNotFoundException;
import com.project.cozystay.payment.exception.TossPaymentConfirmException;
import com.project.cozystay.payment.external.toss.TossPaymentClient;
import com.project.cozystay.payment.external.toss.dto.TossConfirmResponse;
import com.project.cozystay.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCommandService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final TossPaymentClient tossPaymentClient;

    // 결제 생성
    @Transactional
    public Payment createPayment(Long bookingId, Long payerId, PaymentMethod method) {
        Booking booking = bookingRepository.findByIdWithAccommodation(bookingId)
                .orElseThrow(()->new BookingNotFoundException("예약을 찾을 수 없습니다."));

        if(!booking.getGuestId().equals(payerId)){
            throw new PaymentInvalidStateException("본인의 예약만 결제할 수 있습니다.");
        }

        if(booking.getStatus() != BookingStatus.PENDING){
            throw new PaymentInvalidStateException("PENDING 예약만 결제 할 수 있습니다. status=" + booking.getStatus());
        }

        BigDecimal amount = booking.getTotalPrice();

        // 기존 결제 조회
        Payment existing = paymentRepository.findByBooking_Id(bookingId).orElse(null);

        if(existing != null){
            switch(existing.getStatus()){
                case SUCCESS -> throw new PaymentAlreadyExistsException("이미 결제 완료된 예약입니다.");

                case READY -> {
                    // 이미 생성된 READY 결제를 그대로 재사용
                    if(existing.getAmount().compareTo(amount) != 0){
                        throw new PaymentInvalidStateException("기존 결제 금액과 현재 결제 금액이 일치하지 않습니다.");
                    }

                    if(existing.getPaymentMethod() != method){
                        existing.retry(amount, method);
                        existing.assignOrderId(generateOrderId());
                    }

                    return existing;
                }

                case FAILED, CANCELLED -> {
                    // 기존 결제를 READY로 리셋
                    existing.retry(amount, method);
                    existing.assignOrderId(generateOrderId());
                    return existing;
                }
                default -> throw new PaymentInvalidStateException("처리할 수 없는 결제 상태입니다. status=" + existing.getStatus());
            }
        }

        // 신규 결제 생성
        Payment payment = Payment.create(booking, payerId, amount, method);
        payment.assignOrderId(generateOrderId());
        return paymentRepository.save(payment);
    }

    // 토스 결제 승인 처리
    @Transactional
    public Payment confirmPayment(String orderId, Long payerId, String paymentKey, BigDecimal amount){
        Payment payment = paymentRepository.findByOrderIdWithBooking(orderId)
                .orElseThrow(()-> new PaymentNotFoundException("결제를 찾을 수 없습니다."));

        if(!payment.getPayerId().equals(payerId)){
            throw new PaymentInvalidStateException("결제자만 결제 확정 처리를 할 수 있습니다.");
        }

        if(payment.getStatus() != PaymentStatus.READY){
            throw new PaymentInvalidStateException("결제 가능한 상태가 아닙니다. status=" + payment.getStatus());
        }

        if(amount == null || payment.getAmount().compareTo(amount) != 0){
            throw new PaymentInvalidStateException("결제 금액이 일치하지 않습니다.");
        }

        // 토스 승인 API 호출
        TossConfirmResponse tossResponse;
        try{
            tossResponse = tossPaymentClient.confirmPayment(paymentKey, orderId, amount);
        }catch(TossPaymentConfirmException e){
            log.warn("[PAYMENT CONFIRM FAIL] orderId={}, paymentKey={}, reason={}",
                    orderId, paymentKey, e.getMessage());
            payment.markFailed();
            throw e;
        }

        // 승인 성공 시
        payment.markSuccess(tossResponse.getPaymentKey());
        payment.getBooking().confirmByHost();

        return payment;
    }

    // 결제 실패 처리
    @Transactional
    public Payment failPayment(Long paymentId, Long payerId){
        Payment payment = paymentRepository.findByIdWithBooking(paymentId)
                .orElseThrow(()-> new PaymentNotFoundException("결제를 찾을 수 없습니다."));

        if(!payment.getPayerId().equals(payerId)){
            throw new PaymentInvalidStateException("결제자만 결제 실패 처리를 할 수 있습니다.");
        }

        payment.markFailed();

        return payment;
    }

    // 환불 처리
    @Transactional
    public void refundByBooking(Long bookingId){
        paymentRepository.findByBooking_Id(bookingId).ifPresent(Payment::refund);
    }

    private String generateOrderId() {
        return "order_" + java.util.UUID.randomUUID().toString();
    }
}
