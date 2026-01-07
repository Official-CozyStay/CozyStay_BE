package com.project.cozystay.favorite.repository;


import com.project.cozystay.favorite.domain.FavoriteAccommodation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteAccommodationRepository extends JpaRepository<FavoriteAccommodation, Long> {

    Optional<FavoriteAccommodation> findByFavorite_IdAndAccommodation_Id(Long favoriteId, Long accommodationId);

}
