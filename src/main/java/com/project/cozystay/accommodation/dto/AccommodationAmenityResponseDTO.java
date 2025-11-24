package com.project.cozystay.accommodation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationAmenityResponseDTO {
    private Long accommodationId;
    private int count;
    private String message;
}
