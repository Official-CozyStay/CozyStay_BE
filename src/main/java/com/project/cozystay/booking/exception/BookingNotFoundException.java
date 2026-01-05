package com.project.cozystay.booking.exception;

// 404 Not Found
public class BookingNotFoundException extends RuntimeException{

    public BookingNotFoundException(Long bookingId){
        super("예약을 찾을 수 없습니다. bookingId=" + bookingId);
    }

    public BookingNotFoundException(String message){
        super(message);
    }
}
