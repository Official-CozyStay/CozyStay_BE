package com.project.cozystay.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class AvailabilityResponse {
    //전체 응답 DTO
    private Long accommodationId;
    private LocalDate from;
    private LocalDate to;
    private List<AvailabilityDayResponse> days;
}
