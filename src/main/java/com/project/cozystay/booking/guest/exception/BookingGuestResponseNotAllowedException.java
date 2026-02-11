package com.project.cozystay.booking.guest.exception;

// 초대 응답 불가 상태 (409)
public class BookingGuestResponseNotAllowedException extends RuntimeException{
    public BookingGuestResponseNotAllowedException(String message) {
        super(message);
    }
}
