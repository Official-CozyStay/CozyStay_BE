package com.project.cozystay.booking.service;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.exception.BookingNotFoundException;
import com.project.cozystay.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingCancelCommandService {

    private final BookingRepository bookingRepository;

    @Transactional
    public void cancel(Long bookingId, Long guestId){
        Booking booking = bookingRepository.findByIdAndGuestIdForUpdate(bookingId, guestId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        booking.cancel();
    }
}
