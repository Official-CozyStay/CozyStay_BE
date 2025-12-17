package com.project.cozystay.accommodation.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationUpdateResponseDTO {
    private Long accommodationId;
    private String title;
}
