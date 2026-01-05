package com.project.cozystay.booking.exception;

// 404 Not Found
public class AccommodationNotFoundException extends RuntimeException{

    public AccommodationNotFoundException(Long accommodationId) {
        super("존재하지 않는 숙소입니다. id=" + accommodationId);
    }
}
