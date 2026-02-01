package com.project.cozystay.booking.guest.exception;

// 동반자 초대 없음 404
public class BookingGuestNotFoundException extends RuntimeException {
    public BookingGuestNotFoundException(String message) {
        super(message);
    }
}
