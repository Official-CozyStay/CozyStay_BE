package com.project.cozystay.favorite.dto;

import com.project.cozystay.favorite.domain.Favorite;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteResponseDTO {
    private Long favoriteId;
    private String name;
    private String description;
    private boolean isPrivate;
    private int accommodationCount;

    public static FavoriteResponseDTO from(Favorite favorite){
        return FavoriteResponseDTO.builder()
                .favoriteId(favorite.getId())
                .name(favorite.getName())
                .description(favorite.getDescription())
                .isPrivate(favorite.isPrivate())
                .accommodationCount(favorite.getFavoriteAccommodations().size())
                .build();
    }
}

