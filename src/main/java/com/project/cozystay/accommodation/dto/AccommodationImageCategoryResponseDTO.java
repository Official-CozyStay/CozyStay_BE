package com.project.cozystay.accommodation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class AccommodationImageCategoryResponseDTO {
    private Long categoryId;
    private String name;
    private Integer displayOrder;
}
