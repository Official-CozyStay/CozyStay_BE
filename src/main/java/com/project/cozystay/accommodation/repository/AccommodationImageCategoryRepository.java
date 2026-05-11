package com.project.cozystay.accommodation.repository;

import com.project.cozystay.accommodation.domain.AccommodationImageCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccommodationImageCategoryRepository extends JpaRepository<AccommodationImageCategory, Long> {

    List<AccommodationImageCategory> findByAccommodation_IdOrderByDisplayOrderAscIdAsc(Long accommodationId);

    Optional<AccommodationImageCategory> findByIdAndAccommodation_Id(Long categoryId, Long accommodationId);
}


