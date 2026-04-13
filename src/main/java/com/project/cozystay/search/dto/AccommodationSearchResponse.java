package com.project.cozystay.search.dto;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.search.domain.AccommodationDocument;
import com.querydsl.core.Tuple;
import lombok.Builder;
import lombok.Getter;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.project.cozystay.accommodation.domain.QAccommodation.accommodation;
import static com.project.cozystay.accommodation.domain.QAccommodationImage.accommodationImage;

@Getter
@Builder
public class AccommodationSearchResponse {

    private List<AccommodationInfo> accommodations;
    private long totalElements;
    private int totalPages;

    public static AccommodationSearchResponse from(Page<Tuple> searchResults) {
        List<AccommodationInfo> accommodationInfos = searchResults.getContent().stream()
                .map(tuple -> AccommodationInfo.from(tuple.get(accommodation), tuple.get(accommodationImage.imageUrl)))
                .collect(Collectors.toList());
        return AccommodationSearchResponse.builder()
                .accommodations(accommodationInfos)
                .totalElements(searchResults.getTotalElements())
                .totalPages(searchResults.getTotalPages())
                .build();
    }

    public static AccommodationSearchResponse fromDocuments(Page<AccommodationDocument> documents) {
        List<AccommodationInfo> accommodationInfos = documents.getContent().stream()
                .map(AccommodationInfo::from)
                .collect(Collectors.toList());
        return AccommodationSearchResponse.builder()
                .accommodations(accommodationInfos)
                .totalElements(documents.getTotalElements())
                .totalPages(documents.getTotalPages())
                .build();
    }

    @Getter
    @Builder
    public static class AccommodationInfo {
        private Long id;
        private String title;
        private String description;
        private String address;
        private String state;
        private String city;
        private String district;
        private BigDecimal pricePerNight;
        private String mainImageUrl;
        private Double latitude;
        private Double longitude;
        private Double averageRating;
        private Integer reviewCount;

        public static AccommodationInfo from(Accommodation accommodation, String imageUrl) {
            return AccommodationInfo.builder()
                    .id(accommodation.getId())
                    .title(accommodation.getTitle())
                    .description(accommodation.getDescription())
                    .address(accommodation.getAddress())
                    .state(accommodation.getState())
                    .city(accommodation.getCity())
                    .district(accommodation.getDistrict())
                    .pricePerNight(accommodation.getPricePerNight())
                    .mainImageUrl(imageUrl)
                    .latitude(accommodation.getLatitude() != null ? accommodation.getLatitude().doubleValue() : null)
                    .longitude(accommodation.getLongitude() != null ? accommodation.getLongitude().doubleValue() : null)
                    .averageRating(accommodation.getAverageRating())
                    .reviewCount(accommodation.getReviewCount())
                    .build();
        }

        public static AccommodationInfo from(AccommodationDocument document) {
            return AccommodationInfo.builder()
                    .id(document.getId())
                    .title(document.getTitle())
                    .description(document.getDescription())
                    .address(document.getAddress())
                    .state(document.getState())
                    .city(document.getCity())
                    .district(document.getDistrict())
                    .pricePerNight(BigDecimal.valueOf(document.getPricePerNight()))
                    .mainImageUrl(document.getMainImageUrl())
                    .latitude(document.getLatitude())
                    .longitude(document.getLongitude())
                    .averageRating(document.getAverageRating())
                    .reviewCount(document.getReviewCount())
                    .build();
        }
    }
}
