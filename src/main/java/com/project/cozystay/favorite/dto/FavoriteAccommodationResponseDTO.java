package com.project.cozystay.favorite.dto;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.accommodation.domain.AccommodationImage;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class FavoriteAccommodationResponseDTO {

    private Long accommodationId;
    private String title;
    private String province;
    private String city;
    private String district;
    private String country;
    private BigDecimal pricePerNight;
    private String imageUrl;

    public static FavoriteAccommodationResponseDTO from(Accommodation accommodation){
        return FavoriteAccommodationResponseDTO.builder()
                .accommodationId(accommodation.getId())
                .title(accommodation.getTitle())
                .province(accommodation.getProvince())
                .city(accommodation.getCity())
                .district(accommodation.getDistrict())
                .country(accommodation.getCountry())
                .pricePerNight(accommodation.getPricePerNight())
                .imageUrl(
                        accommodation.getImages().stream()
                                .filter(AccommodationImage::isPrimary) //대표 이미지가 있다면 사용
                                .findFirst()
                                .or(() -> accommodation.getImages().stream().findFirst()) //대표 이미지가 없다면 가장 첫 번째 이미지 사용
                                .map(AccommodationImage::getImageUrl)
                                .orElse(null)
                )
                .build();
    }
}
