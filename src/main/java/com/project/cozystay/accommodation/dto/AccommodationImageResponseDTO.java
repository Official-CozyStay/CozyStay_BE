package com.project.cozystay.accommodation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationImageResponseDTO {
    private Long accommodationId;
    private List<Long> imageId;
    private String message;
}
