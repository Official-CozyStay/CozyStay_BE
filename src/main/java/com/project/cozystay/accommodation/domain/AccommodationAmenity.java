package com.project.cozystay.accommodation.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accommodation_amenities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccommodationAmenity {

    @EmbeddedId
    private AccommodationAmenityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accommodationId")
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("amenityId")
    @JoinColumn(name = "amenity_id")
    private Amenity amenity;

    @Builder
    private AccommodationAmenity(Accommodation accommodation, Amenity amenity) {
        this.accommodation = accommodation;
        this.amenity = amenity;
        this.id = new AccommodationAmenityId(
                accommodation.getAccommodationId(),
                amenity.getAmenityId()
        );
    }

    void assignAccommodation(Accommodation accommodation){
        this.accommodation = accommodation;
    }
}
