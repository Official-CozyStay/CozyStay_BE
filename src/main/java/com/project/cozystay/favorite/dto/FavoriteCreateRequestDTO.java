package com.project.cozystay.favorite.dto;

import com.project.cozystay.user.domain.User;
import com.project.cozystay.favorite.domain.Favorite;
import lombok.Getter;

@Getter
public class FavoriteCreateRequestDTO {
    private String name;
    private String description;
    private boolean isPrivate;

    public Favorite toEntity(User user) {
        return Favorite.builder()
                .name(name)
                .description(description)
                .isPrivate(isPrivate)
                .user(user)
                .build();
    }
}
