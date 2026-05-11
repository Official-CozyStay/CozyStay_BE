package com.project.cozystay.accommodation.dto;
import java.util.List;

public record AccommodationImageDeleteRequestDTO(
        List<Long> imageIds
) {
}
