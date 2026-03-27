package com.project.cozystay.payment.scheduler;

import com.project.cozystay.payment.domain.Payment;
import com.project.cozystay.payment.domain.PaymentStatus;
import com.project.cozystay.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTimeoutScheduler {

    private final PaymentRepository paymentRepository;

    @Value("${cozystay.payment.ready-timeout-minutes:15}")
    private long readyTimeoutMinutes;

    // READY 결제 타임아웃 처리 (15분)
    // READY 상태가 일정 시간 이상 지속되면 Payment CANCELLED + Booking CANCELLED
    // 1분마다 실행
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void cancelTimeoutReadyPayment(){
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(readyTimeoutMinutes);

        List<Payment> targets = paymentRepository.findTimeoutTargetWithBooking(PaymentStatus.READY,cutoff);
        if(targets.isEmpty()) return;

        int cancelledCount = 0;

        for(Payment payment : targets){
            try{
                // READY -> CANCELLED 허용
                payment.refund();
                // 결제 미완료면 예약도 취소
                payment.getBooking().cancel();

                cancelledCount++;
            }catch(Exception e){
                // 한 건 실패로 스케줄러 전체 실패 방지
                log.warn("[PAYMENT TIMEOUT] skip paymentId={}, reason={} ", payment.getId(), e.getMessage());
            }
        }
        log.info("[PAYMENT TIMEOUT] cutoff={}, cancelled={}", cutoff, cancelledCount);
    }
}
