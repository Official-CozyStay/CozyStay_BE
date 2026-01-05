package com.project.cozystay.booking.exception;

public class BookingAlreadyCancelledException extends RuntimeException{
    public BookingAlreadyCancelledException(Long bookingId){
        super("이미 취소된 예약입니다. bookingId = " + bookingId);
    }
}
