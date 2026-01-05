package com.project.cozystay.global.exception;

import com.project.cozystay.booking.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 Bad Request
    @ExceptionHandler({InvalidDateRangeException.class, InvalidGuestCountException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 401 Unauthorized
    @ExceptionHandler(AuthenticationRequiredException.class)
    public ResponseEntity<ErrorResponse> handleAuthRequired(AuthenticationRequiredException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 404 Not Found
    @ExceptionHandler({AccommodationNotFoundException.class, BookingNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    // 409 Conflict
    @ExceptionHandler({BookingConflictException.class, BookingNotAvailableException.class})
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException e){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }
}
