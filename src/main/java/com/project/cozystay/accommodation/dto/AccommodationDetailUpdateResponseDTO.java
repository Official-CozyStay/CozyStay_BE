package com.project.cozystay.accommodation.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccommodationDetailUpdateResponseDTO {
    private Long accommodationId;
    private String message;
}
