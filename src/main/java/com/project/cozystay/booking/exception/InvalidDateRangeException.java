package com.project.cozystay.booking.exception;

import java.time.LocalDate;

// 400 Bad Request
public class InvalidDateRangeException extends RuntimeException{

    public InvalidDateRangeException(String message) {
        super(message);
    }

    public static InvalidDateRangeException of(LocalDate from, LocalDate to){
        return new InvalidDateRangeException(
                String.format("잘못된 날짜 범위입니다. from=%s, to=%s (from은 to보다 이전이어야 합니다.)", from, to)
        );
    }

    public static InvalidDateRangeException checkInOut(LocalDate checkIn, LocalDate checkOut){
        return new InvalidDateRangeException(
                String.format("잘못된 예약 날짜입니다. checkIn=%s, checkOut=%s (checkIn은 checkOut보다 이전이어야 합니다.)", checkIn, checkOut)
        );
    }

}
