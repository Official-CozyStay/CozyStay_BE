package com.project.cozystay.accommodation.domain;


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
    private Long accommodationId;

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
}
