package com.project.cozystay.booking.exception;

public class BookingNotFoundException extends RuntimeException{

    public BookingNotFoundException(String message){
        super(message);
    }
}
