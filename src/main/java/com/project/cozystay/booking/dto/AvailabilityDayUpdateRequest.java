package com.project.cozystay.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityDayUpdateRequest {

    private LocalDate date;
    private Boolean isAvailable;
    private BigDecimal customPrice;
    private Integer minNights;
}
