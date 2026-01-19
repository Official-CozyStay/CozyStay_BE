package com.project.cozystay.accommodation.dto;


import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.domain.AccommodationImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Comparator;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationResponseDTO {

    private Long accommodationId;
    private String accommodationName;
    private String accommodationImage;
    private BigDecimal accommodationPrice;

    public static AccommodationResponseDTO fromEntity(Accommodation entity) {

        String imageUrl = entity.getImages().stream()
                .filter(AccommodationImage::isPrimary)
                .findFirst()
                .or(() -> entity.getImages().stream()
                        .min(Comparator.comparing(AccommodationImage::getId)))
                .map(AccommodationImage::getImageUrl)
                .orElse(null);

        return new AccommodationResponseDTO(
                entity.getId(),
                entity.getTitle(),
                imageUrl,
                entity.getPricePerNight()
        );
    }
}
