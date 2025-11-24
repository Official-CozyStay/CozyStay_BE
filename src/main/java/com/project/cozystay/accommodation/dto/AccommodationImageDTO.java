package com.project.cozystay.accommodation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationImageDTO {
    private Long imageId;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean isPrimary;
}
