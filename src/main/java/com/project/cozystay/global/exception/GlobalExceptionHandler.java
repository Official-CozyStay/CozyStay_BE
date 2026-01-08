package com.project.cozystay.global.exception;

import com.project.cozystay.booking.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ===================== 400 BAD REQUEST ===================== */
    @ExceptionHandler({InvalidDateRangeException.class, InvalidGuestCountException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException e){
        log.warn("BadRequest [{}]: {} ", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    /* ===================== 401 UNAUTHORIZED ===================== */
    @ExceptionHandler(AuthenticationRequiredException.class)
    public ResponseEntity<ErrorResponse> handleAuthRequired(AuthenticationRequiredException e){
        log.warn("Unauthorized [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
    }

    /* ===================== 404 NOT FOUND ===================== */
    @ExceptionHandler({AccommodationNotFoundException.class, BookingNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException e){
        log.warn("NotFound [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    /* ===================== 409 CONFLICT ===================== */
    @ExceptionHandler({BookingConflictException.class, BookingNotAvailableException.class,
            BookingAlreadyCancelledException.class, BookingCancellationNotAllowedException.class,
            BookingDecisionNotAllowedException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException e){
        log.warn("Conflict [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    /* ===================== 500 INTERNAL SERVER ERROR ===================== */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(Exception e) {
        log.error("InternalServerError [{}]", e.getClass().getSimpleName(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("서버 내부 오류가 발생했습니다."));
    }
}
