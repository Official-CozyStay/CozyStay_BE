package com.project.cozystay.search.service;

import com.project.cozystay.search.domain.AccommodationDocument;
import com.project.cozystay.search.dto.AccommodationSearchRequest;
import com.project.cozystay.search.dto.AccommodationSearchResponse;
import com.project.cozystay.search.repository.AccommodationElasticSearchRepository;
import com.project.cozystay.search.repository.AccommodationJPASearchRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final AccommodationElasticSearchRepository accommodationElasticSearchRepository;
    private final AccommodationJPASearchRepository accommodationJPASearchRepository; // JPA 리포지토리 추가

    public AccommodationSearchResponse search(AccommodationSearchRequest request) {
        log.info("검색 요청 수신: title={}, province={}, city={}", request.getTitle(), request.getProvince(), request.getCity());

        // 1. 키워드 추출
        String keyword = Stream.of(request.getTitle(), request.getProvince(), request.getCity(), request.getDistrict())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .findFirst()
                .orElse(null);

        // 2. Elasticsearch 검색 시도 (우선순위 1)
        try {
            if (keyword != null && !keyword.isEmpty()) {
                log.info("Elasticsearch 고도화 검색 실행: keyword={}", keyword);
                List<AccommodationDocument> esResults = accommodationElasticSearchRepository.searchByKeyword(keyword);
                return AccommodationSearchResponse.fromDocuments(esResults);
            } else {
                log.info("검색어가 없어 ES 전체 데이터를 조회합니다.");
                List<AccommodationDocument> allEsResults = StreamSupport.stream(accommodationElasticSearchRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList());
                return AccommodationSearchResponse.fromDocuments(allEsResults);
            }
        } catch (Exception e) {
            // 3. ES 장애 발생 시 JPA(QueryDSL)로 Fallback (우선순위 2)
            log.error("Elasticsearch 장애 발생! JPA(QueryDSL) 검색으로 전환합니다. 사유: {}", e.getMessage());
            
            List<Tuple> jpaResults = accommodationJPASearchRepository.search(request);
            log.info("JPA Fallback 검색 결과 건수: {}건", jpaResults.size());
            
            return AccommodationSearchResponse.from(jpaResults);
        }
    }
}
