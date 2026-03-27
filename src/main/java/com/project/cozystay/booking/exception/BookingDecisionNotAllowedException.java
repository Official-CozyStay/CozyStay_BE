package com.project.cozystay.booking.exception;

import com.project.cozystay.booking.domain.BookingStatus;

public class BookingDecisionNotAllowedException extends RuntimeException {
    public BookingDecisionNotAllowedException(Long bookingId, BookingStatus status) {
        // 호스트가 승인/거절을 할 수 없는 상태. 호스트 결정이라는 행위 자체가 불가능한 상태를 막기 위한 예외
        super(String.format("호스트 처리 불가 상태입니다. bookingId=%d, status=%s", bookingId, status));
    }
}
