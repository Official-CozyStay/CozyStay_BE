package com.project.cozystay.auth;

import com.project.cozystay.user.domain.AuthProvider;
import com.project.cozystay.user.domain.Role;
import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.domain.UserGrade;
import com.project.cozystay.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        String oauthAccessToken = accessToken.getTokenValue();
        Instant expiresAtInstant = accessToken.getExpiresAt();
        LocalDateTime tokenExpiresAt = (expiresAtInstant != null) ?
                LocalDateTime.ofInstant(expiresAtInstant, ZoneId.systemDefault()) : null;

        //String oauthRefreshToken = null;
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = convertToProvider(registrationId);

        User user = saveOrUpdate(attributes, oauthAccessToken, tokenExpiresAt, provider);


        return new CustomOAuth2User(user, attributes);
    }

    private AuthProvider convertToProvider(String registrationId) {

        // 휴대폰 번호 패턴이면 LOCAL
        if (registrationId != null && registrationId.matches("^01[0-9]{8,9}$")) {
            return AuthProvider.LOCAL;
        }

        // 그 외는 소셜 로그인
        return switch (registrationId.toLowerCase()) {
            case "kakao" -> AuthProvider.KAKAO;
            case "naver" -> AuthProvider.NAVER;
            case "google" -> AuthProvider.GOOGLE;
            default -> throw new IllegalArgumentException("지원하지 않는 provider: " + registrationId);
        };
    }

    private User saveOrUpdate(Map<String, Object> attributes,
                              String oauthAccessToken,
                              LocalDateTime kakaoTokenExpiresAt,
                              AuthProvider provider) {

        Long providerId = (Long) attributes.get("id");

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) {
            throw new OAuth2AuthenticationException("카카오 계정 정보가 없습니다.");
        }

        String email = (String) kakaoAccount.get("email");

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String nickname = null;
        String profileImageUrl = null;

        if (profile != null) {
            nickname = (String) profile.get("nickname");
            profileImageUrl = (String) profile.get("profile_image_url");
        }

        // email/nickname 여기서 한 번 더 검증
        if (email == null || nickname == null) {
            throw new OAuth2AuthenticationException("카카오에서 필수 사용자 정보(email 또는 nickname)를 제공하지 않았습니다.");
        }

        logger.info("provider ID를 사용하여 사용자를 찾으려고 시도하는 중: {} (type: {}) and provider: {}",
                    providerId, providerId.getClass().getName(), provider);

        Optional<User> userOptional = userRepository.findByProviderIdAndProvider(providerId.toString(), provider);

        User user;
        if (userOptional.isPresent()) {
            // 기존 회원
            user = userOptional.get();
            user = user.updateNicknameAndProfile(nickname, profileImageUrl)
                    .updateOauthTokens(oauthAccessToken, kakaoTokenExpiresAt);
        } else {
            // 신규 회원
            user = User.builder()
                    .nickName(nickname)
                    .email(email)
                    .isEmailVerified(false)
                    .profileImageUrl(profileImageUrl)
                    .provider(provider)
                    .providerId(providerId.toString())
                    .userRole(Role.USER)
                    .userGrade(UserGrade.BRONZE)
                    .totalCompletedBookings(0)
                    .totalStayedNights(0)
                    .reviewCount(0)
                    .oauthAccessToken(oauthAccessToken)
                    .tokenExpiresAt(kakaoTokenExpiresAt)
                    .build();
            userRepository.save(user);
        }
        return user;
    }
}
