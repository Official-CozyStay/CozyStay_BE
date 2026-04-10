package com.project.cozystay.search.repository;

import com.project.cozystay.search.dto.AccommodationSearchRequest;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccommodationJPASearchRepository {

    Page<Tuple> search(AccommodationSearchRequest request, List<Long> excludedIds, Pageable pageable);
}
