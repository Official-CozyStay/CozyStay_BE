package com.project.cozystay.accommodation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationImageCategoryRequestDTO {
    private String name;
    private Integer displayOrder;
}
