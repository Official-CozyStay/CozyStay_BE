package com.project.cozystay.booking.repository;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.domain.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 스케줄링
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
update Booking b
set b.status = :completed,
    b.updatedAt = :now
where b.status = :confirmed
and b.checkOutDate < :today
""")
            int markCompletedBefore(
                    @Param("today") LocalDate today,
                    @Param("now") LocalDateTime now,
                    @Param("confirmed") BookingStatus confirmed,
                    @Param("completed") BookingStatus completed
    );


    // 특정 숙소에 대해 날짜가 겹치는 예약이 존재하는지 체크
    boolean existsByAccommodation_IdAndCheckInDateBeforeAndCheckOutDateAfter(
            Long accommodationId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    );

    // 필요하면 조회용으로도 사용 가능
    List<Booking> findByAccommodation_IdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
            Long accommodationId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    );

    List<Booking> findByGuestIdOrderByCreatedAtDesc(Long guestId);

    List<Booking> findByGuestIdAndStatusOrderByCreatedAtDesc(Long guestId, BookingStatus status);

    // 내 예약 상세 (본인 것만) - 게스트
    Optional<Booking> findByIdAndGuestId(Long bookingId, Long guestId);

    // 예약 취소
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :bookingId and b.guestId = :guestId")
    Optional<Booking> findByIdAndGuestIdForUpdate(@Param("bookingId")Long bookingId,
                                                  @Param("guestId")Long guestId);

    // 호스트 예약 목록 조회 (최신순)
    @Query("""
select b
from Booking b
join b.accommodation a
where a.hostId = :hostId
order by b.createdAt desc
""")
    List<Booking> findAllByHostIdOrderByCreatedAtDesc(@Param("hostId") Long hostId);

    // 호스트 예약 목록 + 상태 필터
    @Query("""
select b
from Booking b
join b.accommodation a
where a.hostId = :hostId
and (:status is null or b.status = :status)
order by b.createdAt desc
""")
    List<Booking> findAllByHostIdAndStatusOrderByCreatedAtDesc(@Param("hostId") Long hostId,
                                                                @Param("status") BookingStatus status);

    // 호스트 예약 상세 조회 (내 숙소 예약만)
    @Query("""
select b
from Booking b
join b.accommodation a
where b.id = :bookingId
and a.hostId =:hostId
""")
Optional<Booking> findByIdAndHostId(@Param("bookingId") Long bookingId,
                                    @Param("hostId") Long hostId);

    // 호스트 수락/거절 상태 전이
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
select b
from Booking b
join b.accommodation a
where b.id = :bookingId
and a.hostId =:hostId
""")
    Optional<Booking> findByIdAndHostIdForUpdate(
            @Param("bookingId") Long bookingId,
            @Param("hostId") Long hostId
    );

    @Query("""
select b
from Booking b
where b.accommodation.id = :accommodationId
and b.status in :activeStatuses
and b.checkInDate < :endExclusive
and b.checkOutDate > :startInclusive
""")
    List<Booking> findActiveBookingsOverlapping(
            @Param("accommodationId") Long accommodationId,
            @Param("activeStatuses") List<BookingStatus> activeStatuses,
            @Param("startInclusive") LocalDate startInclusive,
            @Param("endExclusive") LocalDate endExclusive
    );

    @Query("""
select count(b) > 0
from Booking b
where b.accommodation.id = :accommodationId
and b.status in :activeStatuses
and b.checkInDate < :endExclusive
and b.checkOutDate > :startInclusive
""")
    boolean existsActiveBookingOverlapping(
            @Param("accommodationId") Long accommodationId,
            @Param("activeStatuses") List<BookingStatus> activeStatuses,
            @Param("startInclusive") LocalDate startInclusive,
            @Param("endExclusive") LocalDate endExclusive
    );

    @Query("""
select b
from Booking b
join fetch b.accommodation a
where b.id = :bookingId
""")
    Optional<Booking> findByIdWithAccommodation(@Param("bookingId") Long bookingId);

    /**
     * 특정 기간에 이미 예약된 숙소 ID 목록을 조회합니다.
     */
    @Query("""
select b.accommodation.id
from Booking b
where b.accommodation.id in :accommodationIds
and b.status in :activeStatuses
and b.checkInDate < :checkOutDate
and b.checkOutDate > :checkInDate
""")
    List<Long> findBookedAccommodationIds(
            @Param("accommodationIds") List<Long> accommodationIds,
            @Param("activeStatuses") List<BookingStatus> activeStatuses,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    /**
     * 특정 기간에 이미 예약된 모든 숙소 ID 목록을 조회합니다.
     */
    @Query("""
select distinct b.accommodation.id
from Booking b
where b.status in :activeStatuses
and b.checkInDate < :checkOutDate
and b.checkOutDate > :checkInDate
""")
    List<Long> findAllBookedAccommodationIdsByDateRange(
            @Param("activeStatuses") List<BookingStatus> activeStatuses,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );
}
