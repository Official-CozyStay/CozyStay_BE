package com.project.cozystay.booking.repository;

import com.project.cozystay.booking.domain.BookingGuest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingGuestRepository extends JpaRepository<BookingGuest, Long> {
    List<BookingGuest> findByBookingId(Long bookingId);
}
