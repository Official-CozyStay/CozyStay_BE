package com.project.cozystay.accommodation.dto;

public record AccommodationPrimaryImageResponseDTO(
        Long accommodationId,
        Long beforePrimaryImageId,
        Long afterPrimaryImageId,
        String message
) {
}
