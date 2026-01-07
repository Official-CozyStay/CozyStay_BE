package com.project.cozystay.search.service;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.search.dto.AccommodationSearchRequest;
import com.project.cozystay.search.dto.AccommodationSearchResponse;
import com.project.cozystay.search.repository.AccommodationSearchRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final AccommodationSearchRepository accommodationSearchRepository;

    public AccommodationSearchResponse search(AccommodationSearchRequest request) {
        List<Tuple> searchResults = accommodationSearchRepository.search(request);
        return AccommodationSearchResponse.from(searchResults);
    }
}
