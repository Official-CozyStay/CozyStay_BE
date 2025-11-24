package com.project.cozystay.accommodation.repository;

import com.project.cozystay.accommodation.domain.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Integer> {
}
