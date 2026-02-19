package com.project.cozystay.booking.exception;

public class ReviewAlreadyExistsException extends RuntimeException {
    public ReviewAlreadyExistsException(Long bookingId) {
        super("이미 해당 예약(ID: " + bookingId + ")에 대한 리뷰를 작성했습니다.");
    }
}
