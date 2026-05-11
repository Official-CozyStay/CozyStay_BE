package com.project.cozystay.favorite.repository;

import com.project.cozystay.favorite.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @Query("""
            SELECT DISTINCT f
            FROM Favorite f
            LEFT JOIN FETCH f.favoriteAccommodations fa
            LEFT JOIN FETCH fa.accommodation a
            WHERE f.user.id = :userId
            """)
    List<Favorite> findAllByUser_Id(@Param("userId") Long userId);

    Optional<Favorite> findByIdAndUser_Id(Long favoriteId, Long userId);

}
