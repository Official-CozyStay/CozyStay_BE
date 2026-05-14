package com.project.cozystay.accommodation.dto;

import jakarta.validation.constraints.NotBlank;

public record AccommodationImageCategoryRequestDTO(
        @NotBlank(message = "카테고리 이름은 필수입니다.")
        String name,

        @jakarta.validation.constraints.Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다.")
        Integer displayOrder
) {
}
