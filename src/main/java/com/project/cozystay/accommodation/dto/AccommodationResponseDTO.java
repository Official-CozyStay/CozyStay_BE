package com.project.cozystay.accommodation.dto;


import com.project.cozystay.accommodation.domain.Accommodation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationResponseDTO {

    private Long accommodationId;
    private String accommodationName;

    public static AccommodationResponseDTO fromEntity(Accommodation accommodation) {
        return new AccommodationResponseDTO(
                accommodation.getId(),
                accommodation.getTitle()
        );
    }
}
