package com.project.cozystay.favorite.dto;

import com.project.cozystay.favorite.repository.projection.FavoriteAccommodationProjection;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class FavoriteAccommodationResponseDTO {

    private Long accommodationId;
    private String title;
    private String state;
    private String city;
    private String district;
    private String country;
    private BigDecimal pricePerNight;
    private String imageUrl;

    public static FavoriteAccommodationResponseDTO from(FavoriteAccommodationProjection projection) {
        return FavoriteAccommodationResponseDTO.builder()
                .accommodationId(projection.accommodationId())
                .title(projection.title())
                .state(projection.state())
                .city(projection.city())
                .district(projection.district())
                .country(projection.country())
                .pricePerNight(projection.pricePerNight())
                .imageUrl(projection.imageUrl())
                .build();
    }
}
