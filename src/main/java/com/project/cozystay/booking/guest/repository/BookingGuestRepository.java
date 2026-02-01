package com.project.cozystay.booking.guest.repository;

import com.project.cozystay.booking.guest.domain.BookingGuest;
import com.project.cozystay.booking.guest.domain.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface BookingGuestRepository extends JpaRepository<BookingGuest, Long> {

    @Query("""
            select count(bg)
            from BookingGuest bg
            where bg.booking.id = :bookingId
    """)
    long countByBooking_Id(Long bookingId);

    // 같은 예약에 같은 이메일 중복 초대 방지
    boolean existsByBooking_IdAndGuestEmail(Long bookingId, String guestEmail);

    // 게스트 조회
    List<BookingGuest> findAllByBooking_IdOrderByInvitedAtAsc(Long bookingId);

    // 게스트 초대 삭제
    Optional<BookingGuest> findByIdAndBooking_Id(Long bookingGuestId, Long bookingId);

    // 내가 받은 초대 목록
    Page<BookingGuest> findAllByGuestUserId(Long guestUserId, Pageable pageable);
    Page<BookingGuest> findAllByGuestUserIdAndInvitationStatus(Long guestUserId, InvitationStatus status, Pageable pageable);

}
