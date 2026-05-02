package com.project.cozystay.accommodation.dto;

public record AccommodationDetailUpdateRequestDTO(

        Integer roomCount,
        Integer bedroomCount,
        Integer bedCount,
        Integer bathroomCount,
        Integer airConditionerCount,
        Integer hairDryerCount,
        Integer refrigeratorCount,
        Integer televisionCount,
        Integer washerCount,
        Integer dryerCount,
        Boolean wifiAvailable,
        Boolean parkingAvailable,
        Boolean petAvailable,
        Boolean kitchenAvailable
) {
}
