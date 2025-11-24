package com.project.cozystay.accommodation.dto;


import com.project.cozystay.accommodation.domain.AccommodationDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationDetailRequestDTO {

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

    public AccommodationDetail toEntity() {
        return AccommodationDetail.builder()
                .roomCount(roomCount)
                .bedroomCount(bedroomCount)
                .bedCount(bedCount)
                .bathroomCount(bathroomCount)
                .airConditionerCount(airConditionerCount)
                .hairDryerCount(hairDryerCount)
                .refrigeratorCount(refrigeratorCount)
                .televisionCount(televisionCount)
                .washerCount(washerCount)
                .dryerCount(dryerCount)
                .wifiAvailable(wifiAvailable != null ? wifiAvailable : false)
                .parkingAvailable(parkingAvailable != null ? parkingAvailable : false)
                .petAvailable(petAvailable != null ? petAvailable : false)
                .kitchenAvailable(kitchenAvailable != null ? kitchenAvailable : false)
                .build();
    }

}
