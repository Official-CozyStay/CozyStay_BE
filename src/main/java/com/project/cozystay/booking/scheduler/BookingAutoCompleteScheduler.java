package com.project.cozystay.booking.scheduler;

import com.project.cozystay.booking.domain.BookingStatus;
import com.project.cozystay.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingAutoCompleteScheduler {

    private final BookingRepository bookingRepository;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    // 매일 새벽 3시(한국시간)에
    // 체크아웃 날짜가 지난 CONFIRMED 예약을 COMPLETED 처리
    @Transactional
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void completeBookings(){
        LocalDate today = LocalDate.now(KST);
        LocalDateTime now = LocalDateTime.now(KST);

        int updated = bookingRepository.markCompletedBefore(
                today,
                now,
                BookingStatus.CONFIRMED,
                BookingStatus.COMPLETED
        );

        if(updated > 0){
            log.info("[SCHEDULER] Completed booking updatedCount={}", updated);
        }
    }
}
