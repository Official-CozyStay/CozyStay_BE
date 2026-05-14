package com.project.cozystay.search.service;

import com.project.cozystay.booking.domain.BookingStatus;
import com.project.cozystay.booking.repository.BookingRepository;
import com.project.cozystay.search.domain.AccommodationDocument;
import com.project.cozystay.search.dto.AccommodationSearchRequest;
import com.project.cozystay.search.dto.AccommodationSearchResponse;
import com.project.cozystay.search.repository.AccommodationElasticSearchRepository;
import com.project.cozystay.search.repository.AccommodationJPASearchRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final AccommodationElasticSearchRepository accommodationElasticSearchRepository;
    private final AccommodationJPASearchRepository accommodationJPASearchRepository;
    private final BookingRepository bookingRepository;

    public AccommodationSearchResponse search(AccommodationSearchRequest request) {
        log.info("검색 요청 수신: title={}, state={}, page={}, size={}", 
                request.getTitle(), request.getState(), request.getPage(), request.getSize());

        // 페이징 객체 생성 (기본 정렬: ID 내림차순 - 최신순)
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("id").descending());

        // 1. 제외할 예약된 숙소 ID 조회 (날짜 조건이 있을 경우)
        List<Long> bookedIds = Collections.emptyList();
        if (request.getCheckInDate() != null && request.getCheckOutDate() != null) {
            log.info("예약 필터링을 위한 숙소 ID 조회: {} ~ {}", request.getCheckInDate(), request.getCheckOutDate());
            bookedIds = bookingRepository.findAllBookedAccommodationIdsByDateRange(
                    BookingStatus.getActiveStatuses(),
                    request.getCheckInDate(),
                    request.getCheckOutDate()
            );
            log.info("제외될 예약된 숙소 수: {}건", bookedIds.size());
        }

        // 2. Elasticsearch 검색 시도 (우선순위 1)
        try {
            String title = StringUtils.hasText(request.getTitle()) ? request.getTitle().trim() : null;
            
            log.info("Elasticsearch 검색 실행: title={}, state={}, city={}, district={}, excludedCount={}",
                    title, request.getState(), request.getCity(), request.getDistrict(), bookedIds.size());

            Page<AccommodationDocument> esPage = accommodationElasticSearchRepository.searchAccommodations(
                    title,
                    request.getState(),
                    request.getCity(),
                    request.getDistrict(),
                    bookedIds,
                    pageable
            );

            return AccommodationSearchResponse.fromDocuments(esPage);

        } catch (Exception e) {
            // 3. ES 장애 발생 시 JPA(QueryDSL)로 Fallback
            log.error("Elasticsearch 장애 발생! JPA(QueryDSL) 검색으로 전환합니다. 사유: {}", e.getMessage());
            Page<Tuple> jpaPage = accommodationJPASearchRepository.search(request, bookedIds, pageable);
            return AccommodationSearchResponse.from(jpaPage);
        }
    }
}
