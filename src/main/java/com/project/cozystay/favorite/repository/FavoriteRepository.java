package com.project.cozystay.favorite.repository;

import com.project.cozystay.favorite.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findAllByUser_Id(Long userId);

    Optional<Favorite> findByIdAndUser_Id(Long favoriteId, Long userId);
}
