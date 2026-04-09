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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
    private final AccommodationJPASearchRepository accommodationJPASearchRepository;
    private final BookingRepository bookingRepository;

    public AccommodationSearchResponse search(AccommodationSearchRequest request) {
        log.info("검색 요청 수신: title={}, province={}, checkIn={}, checkOut={}", 
                request.getTitle(), request.getProvince(), request.getCheckInDate(), request.getCheckOutDate());

        // 1. 키워드 추출
        String keyword = Stream.of(request.getTitle(), request.getProvince(), request.getCity(), request.getDistrict())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(" "));

        if (keyword.isEmpty()) {
            keyword = null;
        }

        // 2. Elasticsearch 검색 시도 (우선순위 1)
        try {
            List<AccommodationDocument> esResults;
            if (keyword != null && !keyword.isEmpty()) {
                log.info("Elasticsearch 고도화 검색 실행: keyword={}", keyword);
                esResults = accommodationElasticSearchRepository.searchByKeyword(keyword);
            } else {
                log.info("검색어가 없어 ES 전체 데이터를 조회합니다.");
                esResults = StreamSupport.stream(accommodationElasticSearchRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList());
            }

            // 3. 날짜 가용성 필터링 (Hybrid Search 전략)
            if (request.getCheckInDate() != null && request.getCheckOutDate() != null && !esResults.isEmpty()) {
                log.info("날짜 가용성 필터링 시작: {} ~ {}", request.getCheckInDate(), request.getCheckOutDate());
                
                List<Long> candidateIds = esResults.stream()
                        .map(AccommodationDocument::getId)
                        .collect(Collectors.toList());

                // 해당 기간에 이미 예약(PENDING, CONFIRMED)이 있는 숙소 ID들을 조회
                List<Long> bookedIds = bookingRepository.findBookedAccommodationIds(
                        candidateIds,
                        Arrays.asList(BookingStatus.PENDING, BookingStatus.CONFIRMED),
                        request.getCheckInDate(),
                        request.getCheckOutDate()
                );

                // 예약된 숙소 제외
                esResults = esResults.stream()
                        .filter(doc -> !bookedIds.contains(doc.getId()))
                        .collect(Collectors.toList());
                
                log.info("가용성 필터링 완료: {}건 제외됨, 최종 {}건", bookedIds.size(), esResults.size());
            }

            return AccommodationSearchResponse.fromDocuments(esResults);

        } catch (Exception e) {
            // 4. ES 장애 발생 시 JPA(QueryDSL)로 Fallback
            log.error("Elasticsearch 장애 발생! JPA(QueryDSL) 검색으로 전환합니다. 사유: {}", e.getMessage());
            List<Tuple> jpaResults = accommodationJPASearchRepository.search(request);
            return AccommodationSearchResponse.from(jpaResults);
        }
    }
}
