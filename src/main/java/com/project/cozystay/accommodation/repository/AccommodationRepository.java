package com.project.cozystay.accommodation.repository;

import com.project.cozystay.accommodation.domain.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    @Query("""
    SELECT DISTINCT a
    FROM Accommodation a
    LEFT JOIN FETCH a.images
    WHERE a.status = com.project.cozystay.accommodation.domain.AccommodationStatus.ACTIVE
""")
    List<Accommodation> findAllAccommodations();

    @Query("""
SELECT DISTINCT a FROM Accommodation a
LEFT JOIN FETCH a.detail
LEFT JOIN FETCH a.images
LEFT JOIN FETCH a.amenities am
LEFT JOIN FETCH am.amenity
WHERE a.id = :id
""")
    Optional<Accommodation> findDetailById(Long id);
}
