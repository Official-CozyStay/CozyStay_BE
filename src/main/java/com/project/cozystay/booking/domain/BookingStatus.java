package com.project.cozystay.booking.domain;

import java.util.List;

public enum BookingStatus {
    PENDING,
    CONFIRMED,
    REJECTED,
    CANCELLED,
    COMPLETED;

    /**
     * 예약 가능 여부를 판단할 때 '예약됨'으로 간주하는 활성 상태 목록
     */
    public static List<BookingStatus> getActiveStatuses() {
        return List.of(PENDING, CONFIRMED);
    }
}
