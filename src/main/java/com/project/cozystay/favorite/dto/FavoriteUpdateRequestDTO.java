package com.project.cozystay.favorite.dto;

import lombok.Getter;

@Getter
public class FavoriteUpdateRequestDTO {
    private String name;
    private String description;
    private Boolean isPrivate;
}
