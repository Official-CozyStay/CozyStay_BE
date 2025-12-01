package com.project.cozystay.accommodation.dto;

import com.project.cozystay.accommodation.domain.Accommodation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationFullResponseDTO {

    private Long accommodationId;

    private Long hostId;

    private String title;

    private String description;

    private String accommodationType;

    private String address;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Integer maxGuests;

    private BigDecimal pricePerNight;

    private BigDecimal cleaningFee;

    private BigDecimal serviceFeePercentage;

    private Boolean instantBooking;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    private AccommodationDetailInfoDTO detail;

    private List<AccommodationImageDTO> images;
    private List<AmenityDTO> amenities;

    public static AccommodationFullResponseDTO fromEntity(Accommodation entity) {

        return AccommodationFullResponseDTO.builder()
                .accommodationId(entity.getAccommodationId())
                .hostId(entity.getHostId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .accommodationType(entity.getAccommodationType().name())
                .address(entity.getAddress())
                .city(entity.getCity())
                .state(entity.getState())
                .country(entity.getCountry())
                .postalCode(entity.getPostalCode())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .maxGuests(entity.getMaxGuests())
                .pricePerNight(entity.getPricePerNight())
                .cleaningFee(entity.getCleaningFee())
                .serviceFeePercentage(entity.getServiceFeePercentage())
                .instantBooking(entity.getInstantBooking())
                .checkInTime(entity.getCheckInTime())
                .checkOutTime(entity.getCheckOutTime())

                .detail(entity.getDetail() != null ?
                                AccommodationDetailInfoDTO.fromEntity(entity.getDetail())
                                : null
                )

                .images(
                        entity.getImages().stream()
                                .map(i -> AccommodationImageDTO.builder()
                                        .imageId(i.getImageId())
                                        .imageUrl(i.getImageUrl())
                                        .displayOrder(i.getDisplayOrder())
                                        .isPrimary(i.isPrimary())
                                        .build()
                                ).toList()
                )
                .amenities(
                        entity.getAmenities().stream()
                                .map(a -> AmenityDTO.builder()
                                        .amenityId(a.getAmenity().getAmenityId())
                                        .name(a.getAmenity().getName())
                                        .icon(a.getAmenity().getIcon())
                                        .category(a.getAmenity().getCategory())
                                        .build()
                                ).toList()
                )
                .build();
    }

}
