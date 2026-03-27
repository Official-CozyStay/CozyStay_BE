package com.project.cozystay.accommodation.repository;

import com.project.cozystay.accommodation.domain.AccommodationAmenity;
import com.project.cozystay.accommodation.domain.AccommodationAmenityId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationAmenityRepository extends JpaRepository <AccommodationAmenity, AccommodationAmenityId> {
}
