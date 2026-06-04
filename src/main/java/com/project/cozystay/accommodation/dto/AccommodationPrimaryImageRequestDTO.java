package com.project.cozystay.accommodation.dto;

import jakarta.validation.constraints.NotNull;

public record AccommodationPrimaryImageRequestDTO(
        @NotNull(message = "이미지 ID는 필수입니다.")
        Long imageId
) {
}
