package com.project.cozystay.booking.service;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.booking.exception.BookingNotFoundException;
import com.project.cozystay.booking.exception.InstantBookingDecisionNotAllowedException;
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

        if (Boolean.TRUE.equals(booking.getAccommodation().getInstantBooking())) {
            throw new InstantBookingDecisionNotAllowedException(String.format("즉시예약(instant booking) 예약은 호스트 승인/거절 대상이 아닙니다. bookingId=%d", bookingId));
        }

        booking.confirmByHost();
    }

    @Transactional
    public void reject(Long bookingId, Long hostId) {
        Booking booking = bookingRepository.findByIdAndHostIdForUpdate(bookingId, hostId)
                .orElseThrow(()->new BookingNotFoundException(bookingId));

        if (Boolean.TRUE.equals(booking.getAccommodation().getInstantBooking())) {
            throw new InstantBookingDecisionNotAllowedException(String.format("즉시예약(instant booking) 예약은 호스트 승인/거절 대상이 아닙니다. bookingId=%d", bookingId));
        }
        booking.rejectByHost();

        // 호스트 거절 시 자동 환불
        paymentCommandService.refundByBooking(bookingId);
    }
}
