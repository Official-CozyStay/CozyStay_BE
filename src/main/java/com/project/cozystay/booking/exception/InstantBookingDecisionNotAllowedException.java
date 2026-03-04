package com.project.cozystay.booking.exception;

public class InstantBookingDecisionNotAllowedException extends RuntimeException{
    public InstantBookingDecisionNotAllowedException(String message){
        super(message);
    }
}
