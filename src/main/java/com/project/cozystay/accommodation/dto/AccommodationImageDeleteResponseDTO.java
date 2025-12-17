package com.project.cozystay.accommodation.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccommodationImageDeleteResponseDTO {
    private List<Long> imageIds;
    private String message;
}
