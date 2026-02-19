package com.project.cozystay.booking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityUpdateRequest {

    @NotEmpty(message = "days는 비어있을 수 없습니다.")
    @Valid
    private List<AvailabilityDayUpdateRequest> days;
}
