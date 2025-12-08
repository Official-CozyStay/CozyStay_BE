package com.project.cozystay.review.domain;

import com.project.cozystay.common.BaseTimeEntity;
import com.project.cozystay.review.dto.UserReviewCreateRequest;
import com.project.cozystay.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "user_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewerHost", nullable = false)
    private User reviewerHost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "targetGuest", nullable = false)
    private User targetGuest;


//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "booking_id", unique = true, nullable = false)
//    private Booking booking;
//
    @Column(name = "rating", nullable = false, precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    // ==== 생성자 ====
    @Builder(access = AccessLevel.PRIVATE)
    public UserReview(User reviewerHost, User targetGuest,
                      //Booking booking,
                      BigDecimal rating, String comment) {
        this.reviewerHost = reviewerHost;
        this.targetGuest = targetGuest;
        //this.booking = booking;
        this.rating = rating;
        this.comment = comment;
    }

    // ==== 정적 팩토리 메서드 ====
    public static UserReview of(User reviewerHost, User targetUser,
                                //Booking booking,
                                BigDecimal rating, String comment) {

        return UserReview.builder()
                .reviewerHost(reviewerHost).targetGuest(targetUser)
                .rating(rating).comment(comment)
                .build();
    }

    // ==== 비즈니스 메서드 ====
    public void update(UserReviewCreateRequest request){
        this.rating = request.rating();
        this.comment = request.comment();
    }
}
