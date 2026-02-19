package com.project.cozystay.favorite.dto;

import com.project.cozystay.favorite.domain.Favorite;
import com.project.cozystay.favorite.domain.FavoriteAccommodation;
import lombok.*;

import java.util.List;

@Getter
@Builder
public class FavoriteDetailResponseDTO {

    private Long favoriteId;
    private String name;
    private String description;
    private boolean isPrivate;
    private List<FavoriteAccommodationResponseDTO> accommodations;

    public static FavoriteDetailResponseDTO from(Favorite favorite) {
        return FavoriteDetailResponseDTO.builder()
                .favoriteId(favorite.getId())
                .name(favorite.getName())
                .description(favorite.getDescription())
                .isPrivate(favorite.isPrivate())
                .accommodations(
                        favorite.getFavoriteAccommodations().stream()
                                .map(FavoriteAccommodation::getAccommodation)
                                .map(FavoriteAccommodationResponseDTO::from)
                                .toList()
                )
                .build();
    }
}
