package com.project.cozystay.accommodation.repository;

import com.project.cozystay.accommodation.domain.AccommodationImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccommodationImageRepository extends JpaRepository <AccommodationImage, Long> {
    @Query("""
            SELECT ai
            FROM AccommodationImage ai
            WHERE ai.id IN :imageIds AND ai.accommodation.id = :accommodationId
            """)
    List<AccommodationImage> findAllByIdInAndAccommodationId(
            @Param("imageIds") List<Long> imageIds,
            @Param("accommodationId") Long accommodationId);

    Optional<AccommodationImage> findByIdAndAccommodationId(Long imageId, Long accommodationId);
}
