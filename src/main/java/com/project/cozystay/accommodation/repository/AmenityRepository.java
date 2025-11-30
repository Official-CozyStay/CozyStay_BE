package com.project.cozystay.accommodation.repository;

import com.project.cozystay.accommodation.domain.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AmenityRepository extends JpaRepository<Amenity, Integer> {

    Optional<Amenity> findByName(String name);
}
