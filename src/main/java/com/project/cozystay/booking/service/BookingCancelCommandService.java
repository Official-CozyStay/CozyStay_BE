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
public class BookingCancelCommandService {

    private final BookingRepository bookingRepository;
    private final PaymentCommandService paymentCommandService;

    @Transactional
    public void cancel(Long bookingId, Long guestId){
        Booking booking = bookingRepository.findByIdAndGuestIdForUpdate(bookingId, guestId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        booking.cancel();

        // 자동 환불 처리
        paymentCommandService.refundByBooking(bookingId);
    }
}
