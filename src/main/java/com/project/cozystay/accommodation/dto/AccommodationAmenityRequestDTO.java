package com.project.cozystay.accommodation.dto;

import com.project.cozystay.accommodation.domain.Amenity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationAmenityRequestDTO {

    private String name;
    private String icon;
    private String category;

    public Amenity toEntity(){
        return Amenity.builder()
                .name(name)
                .icon(icon)
                .category(category)
                .build();
    }
}
