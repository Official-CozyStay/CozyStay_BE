package com.project.cozystay.accommodation.repository;

import com.project.cozystay.accommodation.domain.AccommodationImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccommodationImageRepository extends JpaRepository <AccommodationImage, Long> {
    @Query("""
            SELECT ai
            FROM AccommodationImage ai
            WHERE ai.id IN :imageids AND ai.accommodation.id = :accommodationId
            """)
    List<AccommodationImage> findAllByIdInAndAccommodationId(List<Long> imageIds, Long accommodationId);
}
