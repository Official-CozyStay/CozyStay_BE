package com.project.cozystay.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityUpdateRequest {
    private List<AvailabilityDayUpdateRequest> days;
}
