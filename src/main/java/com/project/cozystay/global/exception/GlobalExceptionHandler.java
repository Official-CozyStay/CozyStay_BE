package com.project.cozystay.global.exception;

import com.project.cozystay.booking.exception.BookingNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<?> handleBookingNotFound(BookingNotFoundException e){

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false,
                        "message", e.getMessage()));
    }
}
