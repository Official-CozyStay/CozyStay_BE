package com.project.cozystay.favorite.repository.projection;

import java.math.BigDecimal;

public record FavoriteAccommodationProjection(
        Long accommodationId,
        String title,
        String state,
        String city,
        String district,
        String country,
        BigDecimal pricePerNight,
        String imageUrl
) {
}