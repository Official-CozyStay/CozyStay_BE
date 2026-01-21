package com.project.cozystay.booking.exception;

// 400
public class InvalidAvailabilityRequestException extends RuntimeException {
    public InvalidAvailabilityRequestException(String message) {
        super(message);
    }
}
