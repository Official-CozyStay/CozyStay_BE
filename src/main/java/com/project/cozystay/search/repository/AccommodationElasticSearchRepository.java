package com.project.cozystay.search.repository;

import com.project.cozystay.search.domain.AccommodationDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

/**
 * 숙소 검색의 기본 CRUD 담당
 */
public interface AccommodationElasticSearchRepository extends ElasticsearchRepository<AccommodationDocument, Long>, AccommodationElasticSearchRepositoryCustom {

    List<AccommodationDocument> findByTitleOrAddress(String title, String address);
}
