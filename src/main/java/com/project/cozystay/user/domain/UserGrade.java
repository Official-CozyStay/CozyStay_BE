package com.project.cozystay.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserGrade {

    BRONZE(0, 0),
    SILVER(5, 10),
    GOLD(10, 20),
    PLATINUM(20, 40);

    private final int minBookings;
    private final int minNights;

    /**
     * 다음 등급을 반환합니다.
     * @return 다음 등급 (최고 등급이면 null 반환)
     */
    public UserGrade getNextGrade() {
        int nextIndex = this.ordinal() + 1;
        // 현재 등급이 마지막(PLATINUM)이면 null 반환
        if (nextIndex >= values().length) {
            return null;
        }
        return values()[nextIndex];
    }

    /**
     * 해당 등급(this)을 달성하기 위해 남은 수치를 계산합니다.
     */
    public int calculateRemainingBookings(int currentBookings) {
        return Math.max(0, this.minBookings - currentBookings);
    }

    public int calculateRemainingNights(int currentNights) {
        return Math.max(0, this.minNights - currentNights);
    }
}
