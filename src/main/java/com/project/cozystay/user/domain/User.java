package com.project.cozystay.user.domain;

import com.project.cozystay.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_provider_provider_id",
                        columnNames = {"provider", "provider_id"}
                )
        }
)
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;

    @Column(nullable = false)
    private String nickName;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role userRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(name = "provider_id", length = 100, nullable = false)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserGrade userGrade;

    /** 등급 계산용 누적 지표 */
    @Column(nullable = false)
    private int totalCompletedBookings; // 완료된 예약 건수

    @Column(nullable = false)
    private int totalStayedNights;      // 총 숙박일수

    @Column(nullable = false)
    private int reviewCount;            // 작성한 리뷰 수

    private String oauthAccessToken;

    private LocalDateTime tokenExpiresAt;

    // ====== 비즈니스 메서드 ====== //

    public User updateNicknameAndProfile(String nickName, String profileImageUrl) {
        if (nickName != null && !nickName.isBlank()) {
            this.nickName = nickName;
        }
        if (profileImageUrl != null && !profileImageUrl.isBlank()) {
            this.profileImageUrl = profileImageUrl;
        }
        return this;
    }

    public User updateOauthTokens(String accessToken, LocalDateTime expiresAt) {
        this.oauthAccessToken = accessToken;
        this.tokenExpiresAt = expiresAt;
        return this;
    }

    public void increaseBookingStats(int nights) {
        this.totalCompletedBookings += 1;
        this.totalStayedNights += nights;
        recalculateGrade();
    }

    public void increaseReviewCount() {
        this.reviewCount += 1;
        recalculateGrade();
    }

    private void recalculateGrade() {

        if (totalCompletedBookings >= 20 || totalStayedNights >= 40) {
            this.userGrade = UserGrade.PLATINUM;
        } else if (totalCompletedBookings >= 10 || totalStayedNights >= 20) {
            this.userGrade = UserGrade.GOLD;
        } else if (totalCompletedBookings >= 5 || totalStayedNights >= 10) {
            this.userGrade = UserGrade.SILVER;
        } else {
            this.userGrade = UserGrade.BRONZE;
        }
    }

    public void changeRole(Role role) {
        this.userRole = role;
    }

}
