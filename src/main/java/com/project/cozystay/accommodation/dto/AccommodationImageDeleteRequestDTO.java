package com.project.cozystay.accommodation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationImageDeleteRequestDTO {
    private List<Long> imageIds;
}
