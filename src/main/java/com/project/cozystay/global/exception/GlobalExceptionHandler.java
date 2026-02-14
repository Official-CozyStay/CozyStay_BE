package com.project.cozystay.global.exception;

import com.project.cozystay.booking.exception.*;
import com.project.cozystay.booking.guest.exception.*;
import com.project.cozystay.comment.exception.CommentNotFoundException;
import com.project.cozystay.review.exception.ReviewUpdateNotAllowedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ===================== 400 BAD REQUEST ===================== */
    @ExceptionHandler({InvalidDateRangeException.class, InvalidGuestCountException.class, InvalidAvailabilityRequestException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException e){
        log.warn("BadRequest [{}]: {} ", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e){
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err->err.getDefaultMessage())
                .orElse("요청 값이 올바르지 않습니다.");
        log.warn("BadRequest [Validation]: {}", msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(msg));
    }

    /* ===================== 401 UNAUTHORIZED ===================== */
    @ExceptionHandler(AuthenticationRequiredException.class)
    public ResponseEntity<ErrorResponse> handleAuthRequired(AuthenticationRequiredException e){
        log.warn("Unauthorized [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
    }

    /* ===================== 403 FORBIDDEN ===================== */
    @ExceptionHandler(java.nio.file.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
        log.warn("Forbidden [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("접근 권한이 없습니다."));
    }

    /* ===================== 404 NOT FOUND ===================== */
    @ExceptionHandler({AccommodationNotFoundException.class, BookingNotFoundException.class,
            BookingGuestNotFoundException.class, CommentNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException e){
        log.warn("NotFound [{}]: {}", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    /* ===================== 409 CONFLICT ===================== */
    @ExceptionHandler({BookingConflictException.class, BookingNotAvailableException.class,
            BookingAlreadyCancelledException.class, BookingCancellationNotAllowedException.class,
            BookingDecisionNotAllowedException.class,
            BookingGuestInvitationNotAllowedException.class, BookingGuestLimitExceededException.class,
            BookingGuestDuplicateInvitationException.class, BookingGuestCancelNotAllowedException.class,
            BookingGuestResponseForbiddenException.class, BookingGuestResponseNotAllowedException.class,
            BookingDecisionNotAllowedException.class, ReviewUpdateNotAllowedException.class
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException e) {
        log.warn("BadRequest [NotReadable]: {}", e.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse("요청 본문이 올바르지 않습니다."));
    }
}
