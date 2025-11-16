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
@Table(name = "users")
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

    @Column(unique = true, nullable = false)
    private Long kakaoId;

    private String kakaoAccessToken;

    private LocalDateTime tokenExpiresAt;


    public User updateNicknameAndProfile(String nickName, String profileImageUrl) {
        this.nickName = nickName;
        this.profileImageUrl = profileImageUrl;
        return this;
    }

    public User updateKakaoTokens(String accessToken, LocalDateTime expiresAt) {
        this.kakaoAccessToken = accessToken;
        this.tokenExpiresAt = expiresAt;
        return this;
    }
}
