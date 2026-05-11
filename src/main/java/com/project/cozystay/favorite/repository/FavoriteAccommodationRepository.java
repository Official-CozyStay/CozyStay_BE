package com.project.cozystay.favorite.repository;


import com.project.cozystay.favorite.domain.FavoriteAccommodation;
import com.project.cozystay.favorite.repository.projection.FavoriteAccommodationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteAccommodationRepository extends JpaRepository<FavoriteAccommodation, Long> {

    Optional<FavoriteAccommodation> findByFavorite_IdAndAccommodation_Id(Long favoriteId, Long accommodationId);

    @Query("""
            SELECT new com.project.cozystay.favorite.repository.projection.FavoriteAccommodationProjection(
                a.id,
                a.title,
                a.state,
                a.city,
                a.district,
                a.country,
                a.pricePerNight,
                i.imageUrl
            )
            FROM FavoriteAccommodation fa
            JOIN fa.accommodation a
            LEFT JOIN a.images i ON i.primary = true
            WHERE fa.favorite.id = :favoriteId
            ORDER BY fa.addedAt DESC
            """)
    List<FavoriteAccommodationProjection> findAccommodationProjectionsByFavoriteId(
            @Param("favoriteId") Long favoriteId
    );

}
