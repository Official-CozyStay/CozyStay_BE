package com.project.cozystay.search.service;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.domain.AccommodationImage;
import com.project.cozystay.accommodation.domain.AccommodationStatus;
import com.project.cozystay.accommodation.repository.AccommodationRepository;
import com.project.cozystay.search.domain.AccommodationDocument;
import com.project.cozystay.search.repository.AccommodationElasticSearchRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchSyncService {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationElasticSearchRepository accommodationElasticSearchRepository;
    private final EntityManager entityManager;

    private static final int BATCH_SIZE = 500;

    /**
     * MySQL의 모든 ACTIVE 숙소 데이터를 Elasticsearch로 벌크 동기화합니다.
     * 페이징 처리를 통해 대량 데이터 로드 시의 OOM 문제를 방지합니다.
     */
    @Transactional(readOnly = true)
    public int syncAll() {
        log.info("Elasticsearch 전체 데이터 동기화 시작 (배치 사이즈: {})...", BATCH_SIZE);

        int totalSyncedCount = 0;
        int pageNumber = 0;
        Page<Accommodation> accommodationPage;

        do {
            Pageable pageable = PageRequest.of(pageNumber, BATCH_SIZE);
            accommodationPage = accommodationRepository.findAllByStatus(AccommodationStatus.ACTIVE, pageable);

            List<AccommodationDocument> documents = accommodationPage.getContent().stream().map(acc -> {
                // 대표 이미지(isPrimary=true)를 우선 선택, 없으면 첫 번째 이미지 사용
                String mainImageUrl = acc.getImages().stream()
                        .filter(AccommodationImage::isPrimary)
                        .findFirst()
                        .map(AccommodationImage::getImageUrl)
                        .orElseGet(() -> acc.getImages().stream()
                                .findFirst()
                                .map(AccommodationImage::getImageUrl)
                                .orElse(null));

                return AccommodationDocument.builder()
                        .id(acc.getId())
                        .title(acc.getTitle())
                        .description(acc.getDescription())
                        .address(acc.getAddress())
                        .state(acc.getState())
                        .city(acc.getCity())
                        .district(acc.getDistrict())
                        .pricePerNight(acc.getPricePerNight() != null ? acc.getPricePerNight().doubleValue() : 0.0)
                        .mainImageUrl(mainImageUrl)
                        .latitude(acc.getLatitude() != null ? acc.getLatitude().doubleValue() : null)
                        .longitude(acc.getLongitude() != null ? acc.getLongitude().doubleValue() : null)
                        .averageRating(acc.getAverageRating())
                        .reviewCount(acc.getReviewCount())
                        .build();
            }).collect(Collectors.toList());

            if (!documents.isEmpty()) {
                accommodationElasticSearchRepository.saveAll(documents);
                totalSyncedCount += documents.size();
                
                // 영속성 컨텍스트에 쌓인 엔티티들을 강제로 메모리에서 비움
                entityManager.clear();
                
                log.info("배치 동기화 진행 중: {}건 완료...", totalSyncedCount);
            }

            pageNumber++;
        } while (accommodationPage.hasNext());

        log.info("Elasticsearch 전체 데이터 동기화 완료! 총 {}건", totalSyncedCount);
        return totalSyncedCount;
    }
}
