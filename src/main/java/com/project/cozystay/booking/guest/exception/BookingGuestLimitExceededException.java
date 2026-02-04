package com.project.cozystay.booking.guest.exception;

public class BookingGuestLimitExceededException extends RuntimeException{
    public BookingGuestLimitExceededException(String message) {
        super(message);
    }
}
