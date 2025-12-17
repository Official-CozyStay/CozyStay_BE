package com.project.cozystay.accommodation.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationDetailUpdateRequestDTO {
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
}
