package com.project.cozystay.accommodation.domain;


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

    public void update(AccommodationDetailUpdateRequestDTO dto) {

        if (dto.getRoomCount() != null) this.roomCount = dto.getRoomCount();

        if (dto.getBedroomCount() != null) this.bedroomCount = dto.getBedroomCount();

        if (dto.getBedCount() != null) this.bedCount = dto.getBedCount();

        if (dto.getBathroomCount() != null) this.bathroomCount = dto.getBathroomCount();

        if (dto.getAirConditionerCount() != null) this.airConditionerCount = dto.getAirConditionerCount();

        if (dto.getHairDryerCount() != null) this.hairDryerCount = dto.getHairDryerCount();

        if (dto.getRefrigeratorCount() != null) this.refrigeratorCount = dto.getRefrigeratorCount();

        if (dto.getTelevisionCount() != null) this.televisionCount = dto.getTelevisionCount();

        if (dto.getWasherCount() != null) this.washerCount = dto.getWasherCount();

        if (dto.getDryerCount() != null) this.dryerCount = dto.getDryerCount();

        if (dto.getWifiAvailable() != null) this.wifiAvailable = dto.getWifiAvailable();

        if (dto.getParkingAvailable() != null) this.parkingAvailable = dto.getParkingAvailable();

        if (dto.getPetAvailable() != null) this.petAvailable = dto.getPetAvailable();

        if (dto.getKitchenAvailable() != null) this.kitchenAvailable = dto.getKitchenAvailable();
    }
}
