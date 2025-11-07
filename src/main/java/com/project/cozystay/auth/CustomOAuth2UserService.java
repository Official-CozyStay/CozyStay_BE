package com.project.cozystay.auth;

import com.project.cozystay.user.domain.Role;
import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 1. (스프링이 알아서 처리한) 카카오 사용자 정보를 가져옵니다.
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 2. 카카오가 준 원본 JSON 데이터를 Map으로 가져옵니다.
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // ⭐️ 카카오 토큰 정보 가져오기 (DB에 저장하기 위해)
        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        String kakaoAccessToken = accessToken.getTokenValue();
        Instant expiresAtInstant = accessToken.getExpiresAt();
        LocalDateTime kakaoTokenExpiresAt = (expiresAtInstant != null) ?
                LocalDateTime.ofInstant(expiresAtInstant, ZoneId.systemDefault()) : null;

        // (참고: RefreshToken은 기본 설정으론 안 올 수도 있습니다. null일 수 있음)
        String kakaoRefreshToken = null;

        // 3. ✨ 7단계: 회원 조회 또는 신규 가입 (핵심 로직)
        User user = saveOrUpdate(attributes, kakaoAccessToken, kakaoRefreshToken, kakaoTokenExpiresAt);

        // 4. 시큐리티가 이 사용자를 인식할 수 있도록 포장합니다. (3번 핸들러로 전달됨)
        //    "nameAttributeKey"를 "id"로 설정해야 3번 핸들러에서 "id"로 꺼낼 수 있습니다.
        return new DefaultOAuth2User(
                Collections.singleton(user.getUserRole()),
                attributes,
                "id" // 카카오의 고유 ID 필드명 (yml의 user-name-attribute와 일치)
        );
    }

    // 7단계(회원 조회/가입)를 처리하는 메서드
    private User saveOrUpdate(Map<String, Object> attributes,
                              String kakaoAccessToken, String kakaoRefreshToken, LocalDateTime kakaoTokenExpiresAt) {

        // 카카오가 준 고유 ID
        Long kakaoId = (Long) attributes.get("id");

        // 카카오 계정 정보 파싱 (카카오 응답 스펙 기준)
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (String) profile.get("nickname");

        String profileImageUrl = (String) profile.get("profile_image_url");

        // DB 조회
        Optional<User> userOptional = userRepository.findByKakaoId(kakaoId);

        User user;
        if (userOptional.isPresent()) {
            // [기존 회원]
            user = userOptional.get();
            user = user.updateNicknameAndProfile(nickname, profileImageUrl) // 예시
                    .updateKakaoTokens(kakaoAccessToken, kakaoTokenExpiresAt);
            userRepository.save(user);
        } else {
            // [신규 회원]
            user = User.builder()
                    .kakaoId(kakaoId)
                    .email(email)
                    .nickName(nickname)
                    .profileImageUrl(profileImageUrl)
                    .kakaoAccessToken(kakaoAccessToken)
                    .tokenExpiresAt(kakaoTokenExpiresAt)
                    .userRole(Role.USER)
                    .build();
            userRepository.save(user);
        }
        return user;
    }
}
