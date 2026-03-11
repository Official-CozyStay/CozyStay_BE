package com.project.cozystay.favorite.dto;

import com.project.cozystay.accommodation.domain.AccommodationImage;
import com.project.cozystay.favorite.domain.Favorite;
import com.project.cozystay.favorite.domain.FavoriteAccommodation;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
public class FavoriteResponseDTO {
    private Long favoriteId;
    private String name;
    private String description;
    private boolean isPrivate;
    private int accommodationCount;
    private String coverImageUrl;

    public static FavoriteResponseDTO from(Favorite favorite) {
        String coverImageUrl = getCoverImageUrl(favorite);
        return FavoriteResponseDTO.builder()
                .favoriteId(favorite.getId())
                .name(favorite.getName())
                .description(favorite.getDescription())
                .isPrivate(favorite.isPrivate())
                .accommodationCount(favorite.getFavoriteAccommodations().size())
                .coverImageUrl(coverImageUrl)
                .build();
    }

    private static String getCoverImageUrl(Favorite favorite) {
        return Optional.ofNullable(favorite.getFavoriteAccommodations())
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .map(FavoriteAccommodation::getAccommodation)
                .flatMap(acc -> acc.getImages().stream()
                        .filter(AccommodationImage::isPrimary)
                        .findFirst()
                        .or(() -> acc.getImages().stream().findFirst())
                        .map(AccommodationImage::getImageUrl))
                .orElse(null);
    }
}

