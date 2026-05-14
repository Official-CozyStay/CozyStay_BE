package com.project.cozystay.accommodation.dto;

import jakarta.validation.constraints.NotBlank;

public record AccommodationAmenityRequestDTO(
        @NotBlank(message = "편의시설 이름은 필수입니다.")
        String name,
        String icon,
        String category
) {
}
