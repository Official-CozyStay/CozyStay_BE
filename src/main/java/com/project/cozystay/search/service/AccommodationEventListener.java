package com.project.cozystay.search.service;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.domain.AccommodationImage;
import com.project.cozystay.accommodation.domain.AccommodationStatus;
import com.project.cozystay.accommodation.event.AccommodationEvent;
import com.project.cozystay.accommodation.repository.AccommodationRepository;
import com.project.cozystay.search.domain.AccommodationDocument;
import com.project.cozystay.search.repository.AccommodationElasticSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccommodationEventListener {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationElasticSearchRepository accommodationElasticSearchRepository;

    /**
     * DB 트랜잭션이 커밋된 후 실행되어 데이터 정합성을 보장합니다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAccommodationEvent(AccommodationEvent event) {
        log.info("숙소 동기화 이벤트 수신: ID={}, Type={}", event.getAccommodationId(), event.getOperationType());

        if (event.getOperationType() == AccommodationEvent.OperationType.SAVE) {
            syncSave(event.getAccommodationId());
        } else if (event.getOperationType() == AccommodationEvent.OperationType.DELETE) {
            syncDelete(event.getAccommodationId());
        }
    }

    private void syncSave(Long accommodationId) {
        accommodationRepository.findById(accommodationId).ifPresent(acc -> {
            // 활성화된 숙소만 ES에 저장/업데이트
            if (acc.getStatus() == AccommodationStatus.ACTIVE) {
                // 대표 이미지(isPrimary=true)를 우선 선택, 없으면 첫 번째 이미지 사용
                String mainImageUrl = acc.getImages().stream()
                        .filter(AccommodationImage::isPrimary)
                        .findFirst()
                        .map(AccommodationImage::getImageUrl)
                        .orElseGet(() -> acc.getImages().stream()
                                .findFirst()
                                .map(AccommodationImage::getImageUrl)
                                .orElse(null));

                AccommodationDocument document = AccommodationDocument.builder()
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

                accommodationElasticSearchRepository.save(document);
                log.info("Elasticsearch 데이터 저장 완료: ID={}", accommodationId);
            } else {
                // 비활성화 상태라면 ES에서 제거
                syncDelete(accommodationId);
            }
        });
    }

    private void syncDelete(Long accommodationId) {
        accommodationElasticSearchRepository.deleteById(accommodationId);
        log.info("Elasticsearch 데이터 삭제 완료: ID={}", accommodationId);
    }
}
