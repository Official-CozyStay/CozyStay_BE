package com.project.cozystay.booking.service;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.exception.BookingNotFoundException;
import com.project.cozystay.booking.repository.BookingRepository;
import com.project.cozystay.payment.service.PaymentCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingHostDecisionCommandService {

    private final BookingRepository bookingRepository;
    private final PaymentCommandService paymentCommandService;

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

        // 호스트 거절 시 자동 환불
        paymentCommandService.refundByBooking(bookingId);
    }
}
