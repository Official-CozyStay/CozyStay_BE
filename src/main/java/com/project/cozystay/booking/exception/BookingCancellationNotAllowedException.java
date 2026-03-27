package com.project.cozystay.booking.exception;

import com.project.cozystay.booking.domain.BookingStatus;

public class BookingCancellationNotAllowedException extends RuntimeException{
    public BookingCancellationNotAllowedException(Long bookingId, BookingStatus status){
        super(String.format("현재 상태에서는 예약 취소가 불가능합니다. bookingId=%d, status=%s", bookingId, status));
    }
}
