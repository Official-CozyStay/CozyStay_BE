package com.project.cozystay.accommodation.dto;

import com.project.cozystay.accommodation.domain.AccommodationDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationDetailInfoDTO {
    private Integer roomCount;

    private Integer bedroomCount;

    private Integer bedCount;

    private Integer bathroomCount;

    private Integer airConditionerCount;

    private Integer hairDryerCount;

    private Integer refrigeratorCount;

    private Integer televisionCount;

    private Integer washerCount;

    private Integer dryerCount;

    private Boolean wifiAvailable;

    private Boolean parkingAvailable;

    private Boolean petAvailable;

    private Boolean kitchenAvailable;

    public static AccommodationDetailInfoDTO fromEntity(AccommodationDetail detail) {

        return AccommodationDetailInfoDTO.builder()
                .roomCount(detail.getRoomCount())
                .bedroomCount(detail.getBedroomCount())
                .bedCount(detail.getBedCount())
                .bathroomCount(detail.getBathroomCount())
                .airConditionerCount(detail.getAirConditionerCount())
                .hairDryerCount(detail.getHairDryerCount())
                .refrigeratorCount(detail.getRefrigeratorCount())
                .televisionCount(detail.getTelevisionCount())
                .washerCount(detail.getWasherCount())
                .dryerCount(detail.getDryerCount())
                .wifiAvailable(detail.isWifiAvailable())
                .parkingAvailable(detail.isParkingAvailable())
                .petAvailable(detail.isPetAvailable())
                .kitchenAvailable(detail.isKitchenAvailable())
                .build();
    }
}
