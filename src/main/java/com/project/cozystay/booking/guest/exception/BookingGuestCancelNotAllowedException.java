package com.project.cozystay.booking.guest.exception;

// 초대 취소 불가 409
public class BookingGuestCancelNotAllowedException extends RuntimeException {

    public BookingGuestCancelNotAllowedException(String message) {
        super(message);
    }
}
