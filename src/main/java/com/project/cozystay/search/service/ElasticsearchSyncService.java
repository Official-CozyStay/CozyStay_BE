package com.project.cozystay.search.service;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.domain.AccommodationImage;
import com.project.cozystay.accommodation.domain.AccommodationStatus;
import com.project.cozystay.accommodation.repository.AccommodationRepository;
import com.project.cozystay.search.domain.AccommodationDocument;
import com.project.cozystay.search.repository.AccommodationElasticSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * MySQL의 모든 ACTIVE 숙소 데이터를 Elasticsearch로 벌크 동기화합니다.
     */
    @Transactional(readOnly = true)
    public int syncAll() {
        log.info("Elasticsearch 전체 데이터 동기화 시작...");

        List<Accommodation> accommodations = accommodationRepository.findAll().stream()
                .filter(acc -> acc.getStatus() == AccommodationStatus.ACTIVE)
                .collect(Collectors.toList());

        List<AccommodationDocument> documents = accommodations.stream().map(acc -> {
            String mainImageUrl = acc.getImages().stream()
                    .findFirst()
                    .map(AccommodationImage::getImageUrl)
                    .orElse(null);

            return AccommodationDocument.builder()
                    .id(acc.getId())
                    .title(acc.getTitle())
                    .description(acc.getDescription())
                    .address(acc.getAddress())
                    .province(acc.getProvince())
                    .city(acc.getCity())
                    .district(acc.getDistrict())
                    .pricePerNight(acc.getPricePerNight() != null ? acc.getPricePerNight().doubleValue() : 0.0)
                    .mainImageUrl(mainImageUrl)
                    .build();
        }).collect(Collectors.toList());

        if (!documents.isEmpty()) {
            accommodationElasticSearchRepository.saveAll(documents);
        }

        log.info("Elasticsearch 전체 데이터 동기화 완료! 총 {}건", documents.size());
        return documents.size();
    }
}
