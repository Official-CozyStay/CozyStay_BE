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

        return new DefaultOAuth2User(
                Collections.singleton(user.getUserRole()),
                attributes,
                "id"
        );
    }

    private User saveOrUpdate(Map<String, Object> attributes,
                              String kakaoAccessToken, String kakaoRefreshToken, LocalDateTime kakaoTokenExpiresAt) {

        Long kakaoId = (Long) attributes.get("id");

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (String) profile.get("nickname");

        String profileImageUrl = (String) profile.get("profile_image_url");

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
