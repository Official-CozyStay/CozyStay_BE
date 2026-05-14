package com.project.cozystay.accommodation.dto;

import jakarta.validation.constraints.Min;

public record AccommodationDetailUpdateRequestDTO(

        @Min(value = 0, message = "방 개수는 0개 이상이어야 합니다.")
        Integer roomCount,

        @Min(value = 0, message = "침실 개수는 0개 이상이어야 합니다.")
        Integer bedroomCount,

        @Min(value = 0, message = "침대 개수는 0개 이상이어야 합니다.")
        Integer bedCount,

        @Min(value = 0, message = "욕실 개수는 0개 이상이어야 합니다.")
        Integer bathroomCount,

        @Min(value = 0, message = "에어컨 개수는 0개 이상이어야 합니다.")
        Integer airConditionerCount,

        @Min(value = 0, message = "헤어드라이어 개수는 0개 이상이어야 합니다.")
        Integer hairDryerCount,

        @Min(value = 0, message = "냉장고 개수는 0개 이상이어야 합니다.")
        Integer refrigeratorCount,

        @Min(value = 0, message = "TV 개수는 0개 이상이어야 합니다.")
        Integer televisionCount,

        @Min(value = 0, message = "세탁기 개수는 0개 이상이어야 합니다.")
        Integer washerCount,

        @Min(value = 0, message = "건조기 개수는 0개 이상이어야 합니다.")
        Integer dryerCount,

        Boolean wifiAvailable,
        Boolean parkingAvailable,
        Boolean petAvailable,
        Boolean kitchenAvailable
) {
}
