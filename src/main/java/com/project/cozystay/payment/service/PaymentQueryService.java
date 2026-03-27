package com.project.cozystay.payment.service;

import com.project.cozystay.payment.domain.Payment;
import com.project.cozystay.payment.exception.PaymentAccessDeniedException;
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

    public Payment getById(Long paymentId, Long requesterUserId) {
        Payment payment = paymentRepository.findByIdWithBooking(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("결제를 찾을 수 없습니다."));

        validateAccess(payment, requesterUserId);
        return payment;
    }

    public Payment getByBookingId(Long bookingId, Long requesterUserId) {
        Payment payment = paymentRepository.findByBookingIdWithBooking(bookingId)
                .orElseThrow(() -> new PaymentNotFoundException("해당 예약의 결제가 존재하지 않습니다."));

        validateAccess(payment, requesterUserId);
        return payment;
    }

    private void validateAccess(Payment payment, Long requesterUserId) {
        Long payerId = payment.getPayerId();
        Long hostId = payment.getBooking().getAccommodation().getHostId();

        boolean allowed = requesterUserId.equals(payerId) || requesterUserId.equals(hostId);
        if(!allowed) {
            throw new PaymentAccessDeniedException("결제 정보 조회 권한이 없습니다.");
        }
    }
}
