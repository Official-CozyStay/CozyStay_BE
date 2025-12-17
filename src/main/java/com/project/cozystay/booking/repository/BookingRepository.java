package com.project.cozystay.booking.repository;

import com.project.cozystay.booking.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 특정 숙소에 대해 날짜가 겹치는 예약이 존재하는지 체크
    boolean existsByAccommodationIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
            Long accommodationId,
            LocalDate checkOutDate,
            LocalDate checkInDate
    );

    // 필요하면 조회용으로도 사용 가능
    List<Booking> findByAccommodationIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
            Long accommodationId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    );
}
