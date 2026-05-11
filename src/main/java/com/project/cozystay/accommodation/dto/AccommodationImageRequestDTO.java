package com.project.cozystay.accommodation.dto;

import jakarta.validation.constraints.NotNull;

public record AccommodationImageRequestDTO(
        @NotNull
        Integer displayOrder,

        Boolean isPrimary,

        Long categoryId
) {
}
