package com.project.cozystay.accommodation.domain;
import com.project.cozystay.accommodation.dto.AccommodationRequestDTO;
import com.project.cozystay.accommodation.dto.AccommodationUpdateRequestDTO;
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
    private Long id;

    @Column(name = "host_id", nullable = false)
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

    @Column(nullable = false, length = 50)
    private String city;     // 시/군 (예: 성남시, 가평군)

    @Column(nullable = false, length = 50)
    private String district; // 구 (예: 분당구)

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
    @Builder.Default
    private BigDecimal cleaningFee =  BigDecimal.ZERO;

    @Column(name = "service_fee_percentage", precision =5, scale = 2)
    @Builder.Default
    private BigDecimal serviceFeePercentage = BigDecimal.ZERO;

    @Column(name = "instant_booking")
    @Builder.Default
    private Boolean instantBooking = false;

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    @Column(name = "average_rating")
    @Builder.Default
    private Double averageRating = 0.0;

    @Column(name = "review_count")
    @Builder.Default
    private Integer reviewCount = 0;

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
    //TODO : S3 이미지 서버 연동 후 적용
    //  if (this.images.isEmpty())
    //      throw new IllegalStateException("이미지가 없습니다.");

        if (this.amenities.isEmpty())
            throw new IllegalStateException("편의시설 정보가 없습니다.");

        this.status = AccommodationStatus.ACTIVE;
    }

    public void markDelete(){
        if(this.status == AccommodationStatus.DELETED){
            throw new IllegalStateException("이미 삭제된 숙소입니다.");
        }
        this.status = AccommodationStatus.DELETED;
    }

    public void validateActiveAccommodation(){
        if (this.status != AccommodationStatus.ACTIVE) {
            throw new IllegalStateException("활성화된 숙소가 아닙니다.");
        }
    }

    public void validateNotDeletedAccommodation(){
        if (this.status == AccommodationStatus.DELETED) {
            throw new IllegalStateException("삭제된 숙소 입니다");
        }
    }

    public void validateHost(Long requestHostId){
        if(!this.hostId.equals(requestHostId)){
            throw new IllegalStateException("요청한 HostId와 숙소의 소유자가 다릅니다.");
        }
    }


    public static Accommodation create(Long hostId, AccommodationRequestDTO request) {
        return Accommodation.builder()
                .hostId(hostId)
                .title(request.title())
                .description(request.description())
                .accommodationType(request.accommodationType())
                .address(request.address())
                .city(request.city())
                .district(request.district())
                .state(request.state())
                .country(request.country())
                .postalCode(request.postalCode())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .maxGuests(request.maxGuests())
                .pricePerNight(request.pricePerNight())
                .cleaningFee(request.cleaningFee() != null ? request.cleaningFee() : BigDecimal.ZERO)
                .instantBooking(request.instantBooking() != null ? request.instantBooking() : false)
                .checkInTime(request.checkInTime())
                .checkOutTime(request.checkOutTime())
                .status(AccommodationStatus.DRAFT)
                .build();
    }

    public void update(AccommodationUpdateRequestDTO dto) {
        if (dto.title() != null) this.title = dto.title();
        if (dto.description() != null) this.description = dto.description();
        if (dto.accommodationType() != null) this.accommodationType = dto.accommodationType();
        if (dto.address() != null) this.address = dto.address();
        if (dto.city() != null) this.city = dto.city();
        if (dto.district() != null) this.district = dto.district();
        if (dto.state() != null) this.state = dto.state();
        if (dto.country() != null) this.country = dto.country();
        if (dto.postalCode() != null) this.postalCode = dto.postalCode();
        if (dto.latitude() != null) this.latitude = dto.latitude();
        if (dto.longitude() != null) this.longitude = dto.longitude();
        if (dto.maxGuests() != null) this.maxGuests = dto.maxGuests();
        if (dto.pricePerNight() != null) this.pricePerNight = dto.pricePerNight();
        if (dto.cleaningFee() != null) this.cleaningFee = dto.cleaningFee();
        if (dto.instantBooking() != null) this.instantBooking = dto.instantBooking();
        if (dto.checkInTime() != null) this.checkInTime = dto.checkInTime();
        if (dto.checkOutTime() != null) this.checkOutTime = dto.checkOutTime();
    }

    public void updateReviewStats(Double averageRating, Integer reviewCount) {
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

}