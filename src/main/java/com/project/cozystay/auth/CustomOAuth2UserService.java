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

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        String kakaoAccessToken = accessToken.getTokenValue();
        Instant expiresAtInstant = accessToken.getExpiresAt();
        LocalDateTime kakaoTokenExpiresAt = (expiresAtInstant != null) ?
                LocalDateTime.ofInstant(expiresAtInstant, ZoneId.systemDefault()) : null;

        String kakaoRefreshToken = null;

        User user = saveOrUpdate(attributes, kakaoAccessToken, kakaoRefreshToken, kakaoTokenExpiresAt);

        return new CustomOAuth2User(user, attributes);
    }

    private User saveOrUpdate(Map<String, Object> attributes,
                              String kakaoAccessToken, String kakaoRefreshToken, LocalDateTime kakaoTokenExpiresAt) {

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

        Optional<User> userOptional = userRepository.findByProviderId(providerId.toString());

        User user;
        // TODO: JPA 더티 체킹 활용
        if (userOptional.isPresent()) {
            // [기존 회원]
            user = userOptional.get();
            user = user.updateNicknameAndProfile(nickname, profileImageUrl)
                    .updateOauthTokens(kakaoAccessToken, kakaoTokenExpiresAt);
            userRepository.save(user);
        } else {
            // [신규 회원]
            user = User.builder()
                    .providerId(providerId.toString())
                    .email(email)
                    .nickName(nickname)
                    .profileImageUrl(profileImageUrl)
                    .oauthAccessToken(kakaoAccessToken)
                    .tokenExpiresAt(kakaoTokenExpiresAt)
                    .userRole(Role.USER)
                    .build();
            userRepository.save(user);
        }
        return user;
    }
}
