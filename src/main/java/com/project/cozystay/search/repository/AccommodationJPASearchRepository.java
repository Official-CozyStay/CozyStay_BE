package com.project.cozystay.search.repository;

import com.project.cozystay.search.dto.AccommodationSearchRequest;
import com.querydsl.core.Tuple;

import java.util.List;

public interface AccommodationJPASearchRepository {

    List<Tuple> search(AccommodationSearchRequest request);
}
