package com.project.cozystay.booking.guest.repository;

import com.project.cozystay.booking.guest.domain.BookingGuest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookingGuestRepository extends JpaRepository<BookingGuest, Long> {

    @Query("""
            select count(bg)
            from BookingGuest bg
            where bg.booking.id = :bookingId
    """)
    long countByBookingId(Long bookingId);

    // 같은 예약에 같은 이메일 중복 초대 방지
    boolean existsByBooking_IdAndGuestEmail(Long bookingId, String guestEmail);

    // 게스트 조회
    List<BookingGuest> findAllByBooking_IdOrderByInvitiedAtAsc(Long bookingId);

    // 게스트 초대 삭제
    Optional<BookingGuest> findByIdAndBooking_Id(Long bookingGuestId, Long bookingId);
}
