package com.project.cozystay.search.repository;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.search.dto.AccommodationSearchRequest;

import java.util.List;

public interface AccommodationSearchRepository {

    List<Accommodation> search(AccommodationSearchRequest request);
}
