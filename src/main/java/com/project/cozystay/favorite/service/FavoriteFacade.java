package com.project.cozystay.favorite.service;

import com.project.cozystay.favorite.domain.Favorite;
import com.project.cozystay.favorite.dto.FavoriteCreateRequestDTO;
import com.project.cozystay.favorite.dto.FavoriteCreateResponseDTO;
import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FavoriteFacade {
    private final FavoriteService favoriteService;
    private final UserService userService;

        public FavoriteCreateResponseDTO createFavorite(Long userId, FavoriteCreateRequestDTO request) {
            User user = userService.getUserRefOrThrow(userId);

            Favorite favorite = request.toEntity(user);

            Long favoriteId = favoriteService.saveFavorite(favorite);

            return FavoriteCreateResponseDTO.builder()
                    .favoriteId(favoriteId)
                    .build();
        }
    }
