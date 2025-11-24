package com.project.cozystay.accommodation.dto;


import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.domain.AccommodationImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationResponseDTO {

    private Long accommodationId;
    private String accommodationName;
    private List<String> accommodationImage;
    private BigDecimal accommodationPrice;

    public static AccommodationResponseDTO fromEntity(Accommodation entity) {
        return new AccommodationResponseDTO(
                entity.getAccommodationId(),
                entity.getTitle(),
                entity.getImage().stream()
                        .map(AccommodationImage::getImageUrl)
                        .toList(),
                entity.getPricePerNight()
        );
    }
}
