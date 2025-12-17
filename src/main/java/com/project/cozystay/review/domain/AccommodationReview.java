package com.project.cozystay.review.domain;

import com.project.cozystay.accommodation.domain.Accommodation;
import com.project.cozystay.common.BaseTimeEntity;
import com.project.cozystay.review.dto.AccommodationReviewCreateRequest;
import com.project.cozystay.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "accommodation_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccommodationReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "booking_id", unique = true, nullable = false)
//    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;

    @Column(name = "rating_overall", nullable = false, precision = 2, scale = 1)
    private BigDecimal ratingOverall;

    @Column(name = "rating_cleanliness", precision = 2, scale = 1)
    private BigDecimal ratingCleanliness;

    @Column(name = "rating_accuracy", precision = 2, scale = 1)
    private BigDecimal ratingAccuracy;

    @Column(name = "rating_checkin", precision = 2, scale = 1)
    private BigDecimal ratingCheckin;

    @Column(name = "rating_communication", precision = 2, scale = 1)
    private BigDecimal ratingCommunication;

    @Column(name = "rating_location", precision = 2, scale = 1)
    private BigDecimal ratingLocation;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;


    // ==== 생성자 ====
    @Builder(access = AccessLevel.PRIVATE)
    public AccommodationReview(//Booking booking,
                               Accommodation accommodation,
                               User guest,
                               BigDecimal ratingOverall, BigDecimal ratingCleanliness,
                               BigDecimal ratingAccuracy, BigDecimal ratingCheckin,
                               BigDecimal ratingCommunication, BigDecimal ratingLocation,
                               String comment) {
//        this.booking = booking;
        this.accommodation = accommodation;
        this.guest = guest;
        this.ratingOverall = ratingOverall;
        this.ratingCleanliness = ratingCleanliness;
        this.ratingAccuracy = ratingAccuracy;
        this.ratingCheckin = ratingCheckin;
        this.ratingCommunication = ratingCommunication;
        this.ratingLocation = ratingLocation;
        this.comment = comment;
    }

    // ==== 정적 팩토리 메서드 ====
    public static AccommodationReview of(//Booking booking,
                                         Accommodation accommodation,
                                         User guest,
                                         BigDecimal ratingOverall, BigDecimal ratingCleanliness,
                                         BigDecimal ratingAccuracy, BigDecimal ratingCheckin,
                                         BigDecimal ratingCommunication, BigDecimal ratingLocation, String comment){

        return AccommodationReview.builder()
                .accommodation(accommodation)
                .guest(guest)
                .ratingOverall(ratingOverall).ratingCleanliness(ratingCleanliness)
                .ratingAccuracy(ratingAccuracy).ratingCheckin(ratingCheckin)
                .ratingCommunication(ratingCommunication).ratingLocation(ratingLocation)
                .comment(comment).build();
    }

    // ==== 비즈니스 메서드 ====
    public void update(AccommodationReviewCreateRequest request, BigDecimal ratingOverall){
        this.ratingOverall = ratingOverall;
        this.ratingCleanliness = request.ratingCleanliness();
        this.ratingAccuracy = request.ratingAccuracy();
        this.ratingCheckin = request.ratingCheckin();
        this.ratingCommunication = request.ratingCommunication();
        this.ratingLocation = request.ratingLocation();
        this.comment = request.comment();
    }
}

