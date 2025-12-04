package com.project.cozystay.accommodation.domain;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "accommodations")
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accommodation_id")
    private Long accommodationId;

    @Column(name = "host_id", nullable = false)
    //ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "host_id")
    private Long hostId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "accommodation_type", nullable = false)
    private AccommodationType accommodationType;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "max_guests", nullable = false)
    private Integer maxGuests;

    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @Column(name = "cleaning_fee", precision = 10, scale = 2)
    private BigDecimal cleaningFee =  BigDecimal.ZERO;

    @Column(name = "service_fee_percentage", precision =5, scale = 2)
    private BigDecimal serviceFeePercentage = BigDecimal.ZERO;

    @Column(name = "instant_booking")
    private Boolean instantBooking = false;

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private AccommodationStatus status;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (status == null)
            status = AccommodationStatus.DRAFT;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @OneToOne(mappedBy = "accommodation", cascade = CascadeType.ALL)
    private AccommodationDetail detail;

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<AccommodationImage> images = new HashSet<>();

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<AccommodationAmenity> amenities = new HashSet<>();

    public void addDetail(AccommodationDetail detail) {
        this.detail = detail;
        detail.assignAccommodation(this);
    }

    public void addImage(AccommodationImage image){
        this.images.add(image);
        image.assignAccommodation(this);
    }

    public void addAmenity(AccommodationAmenity amenity){
        this.amenities.add(amenity);
        amenity.assignAccommodation(this);
    }

    public void publish(){
        if (this.status != AccommodationStatus.DRAFT) {
            throw new IllegalStateException("이미 활성화된 숙소입니다.");
        }

        if (this.detail == null)
            throw new IllegalStateException("상세 정보가 없습니다.");

        if (this.images.isEmpty())
            throw new IllegalStateException("이미지가 없습니다.");

        if (this.amenities.isEmpty())
            throw new IllegalStateException("편의시설 정보가 없습니다.");

        this.status = AccommodationStatus.ACTIVE;
    }

}