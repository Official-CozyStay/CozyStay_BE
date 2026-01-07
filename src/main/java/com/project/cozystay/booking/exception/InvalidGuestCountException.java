package com.project.cozystay.booking.exception;

// 400 Bad Request
public class InvalidGuestCountException extends RuntimeException {

    public InvalidGuestCountException(String message){
        super(message);
    }

    public static InvalidGuestCountException of(int numberOfGuests){
        return new InvalidGuestCountException("잘못된 인원수입니다. numberOfGuests=" + numberOfGuests);
    }
}
