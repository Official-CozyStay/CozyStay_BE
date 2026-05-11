package com.project.cozystay.payment.service;

import com.project.cozystay.payment.domain.Payment;
import com.project.cozystay.payment.exception.PaymentNotFoundException;
import com.project.cozystay.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentFailureService {

    private final PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long paymentId){
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("결제를 찾을 수 없습니다."));

        payment.markFailed();
    }
}
