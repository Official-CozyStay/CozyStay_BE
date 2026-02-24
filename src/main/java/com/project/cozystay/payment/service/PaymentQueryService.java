package com.project.cozystay.payment.service;

import com.project.cozystay.payment.domain.Payment;
import com.project.cozystay.payment.exception.PaymentNotFoundException;
import com.project.cozystay.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryService {

    private final PaymentRepository paymentRepository;

    public Payment getById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("결제를 찾을 수 없습니다."));
    }

    public Payment getByBookingId(Long bookingId) {
        return paymentRepository.findByBooking_Id(bookingId)
                .orElseThrow(() -> new PaymentNotFoundException("해당 예약의 결제가 존재하지 않습니다."));
    }
}
