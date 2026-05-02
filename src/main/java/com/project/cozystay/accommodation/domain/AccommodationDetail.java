package com.project.cozystay.accommodation.domain;


import com.project.cozystay.accommodation.dto.AccommodationDetailRequestDTO;
import com.project.cozystay.accommodation.dto.AccommodationDetailUpdateRequestDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "accommodation_details")

public class AccommodationDetail {
    @Id
    @Column(name = "accommodation_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;
    
    @Column(name = "room_count", nullable = false)
    private int roomCount;

    @Column(name = "bedroom_count", nullable = false)
    private int bedroomCount;

    @Column(name = "bed_count", nullable = false)
    private int bedCount;

    @Column(name = "bathroom_count", nullable = false)
    private int bathroomCount;

    @Column(name = "air_conditioner_count", nullable = false)
    private int airConditionerCount;

    @Column(name = "hairdryer_count", nullable = false)
    private int hairDryerCount;

    @Column(name = "refrigerator_count", nullable = false)
    private int refrigeratorCount;

    @Column(name = "television_count", nullable = false)
    private int televisionCount;

    @Column(name = "washer_count", nullable = false)
    private int washerCount;

    @Column(name = "dryer_count", nullable = false)
    private int dryerCount;

    @Column(name = "wifi_available")
    private boolean wifiAvailable = false;

    @Column(name = "parking_available")
    private boolean parkingAvailable = false;

    @Column(name = "pet_available")
    private boolean petAvailable = false;

    @Column(name = "kitchen_available")
    private boolean kitchenAvailable = false;

    void assignAccommodation(Accommodation accommodation){
        this.accommodation = accommodation;
    }

    public static AccommodationDetail create(AccommodationDetailRequestDTO request) {
        return AccommodationDetail.builder()
                .roomCount(request.roomCount() != null ? request.roomCount() : 0)
                .bedroomCount(request.bedroomCount() != null ? request.bedroomCount() : 0)
                .bedCount(request.bedCount() != null ? request.bedCount() : 0)
                .bathroomCount(request.bathroomCount() != null ? request.bathroomCount() : 0)
                .airConditionerCount(request.airConditionerCount() != null ? request.airConditionerCount() : 0)
                .hairDryerCount(request.hairDryerCount() != null ? request.hairDryerCount() : 0)
                .refrigeratorCount(request.refrigeratorCount() != null ? request.refrigeratorCount() : 0)
                .televisionCount(request.televisionCount() != null ? request.televisionCount() : 0)
                .washerCount(request.washerCount() != null ? request.washerCount() : 0)
                .dryerCount(request.dryerCount() != null ? request.dryerCount() : 0)
                .wifiAvailable(request.wifiAvailable() != null ? request.wifiAvailable() : false)
                .parkingAvailable(request.parkingAvailable() != null ? request.parkingAvailable() : false)
                .petAvailable(request.petAvailable() != null ? request.petAvailable() : false)
                .kitchenAvailable(request.kitchenAvailable() != null ? request.kitchenAvailable() : false)
                .build();
    }

    public void update(AccommodationDetailUpdateRequestDTO dto) {
        if (dto.roomCount() != null) this.roomCount = dto.roomCount();
        if (dto.bedroomCount() != null) this.bedroomCount = dto.bedroomCount();
        if (dto.bedCount() != null) this.bedCount = dto.bedCount();
        if (dto.bathroomCount() != null) this.bathroomCount = dto.bathroomCount();
        if (dto.airConditionerCount() != null) this.airConditionerCount = dto.airConditionerCount();
        if (dto.hairDryerCount() != null) this.hairDryerCount = dto.hairDryerCount();
        if (dto.refrigeratorCount() != null) this.refrigeratorCount = dto.refrigeratorCount();
        if (dto.televisionCount() != null) this.televisionCount = dto.televisionCount();
        if (dto.washerCount() != null) this.washerCount = dto.washerCount();
        if (dto.dryerCount() != null) this.dryerCount = dto.dryerCount();
        if (dto.wifiAvailable() != null) this.wifiAvailable = dto.wifiAvailable();
        if (dto.parkingAvailable() != null) this.parkingAvailable = dto.parkingAvailable();
        if (dto.petAvailable() != null) this.petAvailable = dto.petAvailable();
        if (dto.kitchenAvailable() != null) this.kitchenAvailable = dto.kitchenAvailable();
    }
}
