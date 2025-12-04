package com.project.cozystay.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class AvailabiltiyDayResponse {
    // 하루치 정보 DTO
    private LocalDate date;
    private boolean available;
    private BigDecimal pricePerNight;
    private int minNights;
}
