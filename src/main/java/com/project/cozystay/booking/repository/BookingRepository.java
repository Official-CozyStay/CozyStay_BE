package com.project.cozystay.booking.repository;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 특정 숙소에 대해 날짜가 겹치는 예약이 존재하는지 체크
    boolean existsByAccommodationIdAndCheckInDateBeforeAndCheckOutDateAfter(
            Long accommodationId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    );

    // 필요하면 조회용으로도 사용 가능
    List<Booking> findByAccommodationIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
            Long accommodationId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    );

    // 내 예약 목록
    List<Booking> findByGuestIdOrderByCreatedAtDesc(Long guestId);

    // 상태 필터 포함 목록
    List<Booking> findByGuestIdAndStatusOrderByCreatedAtDesc(Long guestId, BookingStatus status);

    // 내 예약 상세 (본인 것만)
    Optional<Booking> findByIdAndGuestId(Long bookingId, Long guestId);
}
