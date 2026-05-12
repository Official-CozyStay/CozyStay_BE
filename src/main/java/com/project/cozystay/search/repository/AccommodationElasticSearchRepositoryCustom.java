package com.project.cozystay.search.repository;

import com.project.cozystay.search.domain.AccommodationDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 숙소 검색 고도화를 위한 인터페이스
 */
public interface AccommodationElasticSearchRepositoryCustom {
    Page<AccommodationDocument> searchAccommodations(
            String title,
            String state,
            String city,
            String district,
            List<Long> excludedIds,
            Pageable pageable
    );
}
