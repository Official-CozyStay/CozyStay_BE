package com.project.cozystay.review.domain;

import com.project.cozystay.booking.domain.Booking;
import com.project.cozystay.comment.domain.Comment;
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
public class UserReview extends BaseTimeEntity implements Commentable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_host", nullable = false)
    private User reviewerHost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_guest", nullable = false)
    private User targetGuest;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", unique = true, nullable = false)
    private Booking booking;

    @Column(name = "rating", nullable = false, precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    // ==== 생성자 ====
    @Builder(access = AccessLevel.PRIVATE)
    public UserReview(Booking booking, User reviewerHost, User targetGuest,
                      BigDecimal rating, String reviewComment) {
        this.booking = booking;
        this.reviewerHost = reviewerHost;
        this.targetGuest = targetGuest;
        this.rating = rating;
        this.reviewComment = reviewComment;
    }

    // ==== 정적 팩토리 메서드 ====
    public static UserReview of(Booking booking, User reviewerHost, User targetUser,
                                BigDecimal rating, String reviewComment) {

        return UserReview.builder()
                .booking(booking)
                .reviewerHost(reviewerHost).targetGuest(targetUser)
                .rating(rating).reviewComment(reviewComment)
                .build();
    }

    // ==== 비즈니스 메서드 ====
    public void update(UserReviewCreateRequest request){
        this.rating = request.rating();
        this.reviewComment = request.reviewComment();
    }

    public void addComment(Comment comment) {
        this.comment = comment;
    }
}
