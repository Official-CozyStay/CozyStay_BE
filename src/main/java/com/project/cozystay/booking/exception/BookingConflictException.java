package com.project.cozystay.booking.exception;

import java.time.LocalDate;

// 409 Conflict
// 겹치는 예약 존재
public class BookingConflictException extends RuntimeException {

    public BookingConflictException(Long accommodationId, LocalDate checkIn, LocalDate checkOut){
        super(String.format("이미 겹치는 예약이 존재합니다. accommodationId=%d, checkIn=%s, checkOut=%s"
                , accommodationId, checkIn, checkOut));
    }
}
