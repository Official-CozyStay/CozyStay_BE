package com.project.cozystay.booking.repository;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.domain.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    List<Booking> findByGuestIdOrderByCreatedAtDesc(Long guestId);

    List<Booking> findByGuestIdAndStatusOrderByCreatedAtDesc(Long guestId, BookingStatus status);

    // 내 예약 상세 (본인 것만)
    Optional<Booking> findByIdAndGuestId(Long bookingId, Long guestId);

    // 예약 취소
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :bookingId and b.guestId = :guestId")
    Optional<Booking> findByIdAndGuestIdForUpdate(@Param("bookingId")Long bookingId,
                                                  @Param("guestId")Long guestId);
}
