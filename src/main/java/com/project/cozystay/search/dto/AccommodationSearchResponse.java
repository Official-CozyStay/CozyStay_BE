package com.project.cozystay.search.dto;

import com.project.cozystay.accommodation.domain.Accommodation;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class AccommodationSearchResponse {

    private List<AccommodationInfo> accommodations;

    public static AccommodationSearchResponse from(List<Accommodation> accommodations) {
        List<AccommodationInfo> accommodationInfos = accommodations.stream()
                .map(AccommodationInfo::from)
                .collect(Collectors.toList());
        return AccommodationSearchResponse.builder()
                .accommodations(accommodationInfos)
                .build();
    }

    @Getter
    @Builder
    public static class AccommodationInfo {
        private Long id;
        private String title;
        private String description;
        private String address;
        private String city;
        private BigDecimal pricePerNight;
        private String mainImageUrl;

        public static AccommodationInfo from(Accommodation accommodation) {
            String mainImageUrl = accommodation.getImages().stream()
                    .findFirst()
                    .map(com.project.cozystay.accommodation.domain.AccommodationImage::getImageUrl)
                    .orElse(null);
            return AccommodationInfo.builder()
                    .id(accommodation.getId())
                    .title(accommodation.getTitle())
                    .description(accommodation.getDescription())
                    .address(accommodation.getAddress())
                    .city(accommodation.getCity())
                    .pricePerNight(accommodation.getPricePerNight())
                    .mainImageUrl(mainImageUrl)
                    .build();
        }
    }
}
