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
                .roomCount(roomCount != null ? roomCount : 0)
                .bedroomCount(bedroomCount != null ? bedroomCount : 0)
                .bedCount(bedCount != null ? bedCount : 0)
                .bathroomCount(bathroomCount != null ? bathroomCount : 0)
                .airConditionerCount(airConditionerCount != null ? airConditionerCount : 0)
                .hairDryerCount(hairDryerCount != null ? hairDryerCount : 0)
                .refrigeratorCount(refrigeratorCount != null ? refrigeratorCount : 0)
                .televisionCount(televisionCount != null ? televisionCount : 0)
                .washerCount(washerCount != null ? washerCount : 0)
                .dryerCount(dryerCount != null ? dryerCount : 0)
                .wifiAvailable(wifiAvailable != null ? wifiAvailable : false)
                .parkingAvailable(parkingAvailable != null ? parkingAvailable : false)
                .petAvailable(petAvailable != null ? petAvailable : false)
                .kitchenAvailable(kitchenAvailable != null ? kitchenAvailable : false)
                .build();
    }

}
