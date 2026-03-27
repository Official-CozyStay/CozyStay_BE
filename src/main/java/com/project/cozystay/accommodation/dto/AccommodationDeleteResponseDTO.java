package com.project.cozystay.accommodation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AccommodationDeleteResponseDTO {
    private Long accommodationId;
    private String message;
}
