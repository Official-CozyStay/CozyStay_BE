package com.project.cozystay.booking.guest.exception;

// 초대 응답 권한 없음 409
public class BookingGuestResponseForbiddenException extends RuntimeException{
    public BookingGuestResponseForbiddenException(String message) {
        super(message);
    }
}
