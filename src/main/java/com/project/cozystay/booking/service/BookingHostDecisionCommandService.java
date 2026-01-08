package com.project.cozystay.booking.service;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.exception.BookingNotFoundException;
import com.project.cozystay.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingHostDecisionCommandService {

    private final BookingRepository bookingRepository;

    @Transactional
    public void confirm(Long bookingId, Long hostId) {
        Booking booking = bookingRepository.findByIdAndHostIdForUpdate(bookingId, hostId)
                .orElseThrow(()->new BookingNotFoundException(bookingId));

        booking.confirmByHost();
    }

    @Transactional
    public void reject(Long bookingId, Long hostId) {
        Booking booking = bookingRepository.findByIdAndHostIdForUpdate(bookingId, hostId)
                .orElseThrow(()->new BookingNotFoundException(bookingId));

        booking.rejectByHost();
    }
}
