package com.project.cozystay.auth;

import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

    private final JwtProvider jwtProvider; // (다음 단계) 우리가 만들 JWT 발급 유틸
    private final UserRepository userRepository; // user/ 패키지의 리포지토리

    // application.yml 에 설정한 프론트엔드 URL
    @Value("${redirect.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 1. 2번 서비스에서 포장했던 OAuth2User 객체를 가져옵니다.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 2. 2번 서비스에서 "nameAttributeKey"로 지정했던 "id" (카카오 고유 ID)를 꺼냅니다.
        Long kakaoId = oAuth2User.getAttribute("id");

        log.info("카카오 로그인 성공. Kakao ID: {}", kakaoId);

        // 3. DB에서 이 카카오 ID를 가진 우리 회원을 찾습니다.
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. Kakao ID: " + kakaoId));

        // 4. ✨ 8단계! 우리 CozyStay의 JWT(액세스 토큰)를 발급합니다.
        //    JWT에는 카카오 ID가 아닌, 우리 DB의 PK (user.getId())를 담습니다.
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        // TODO: refreshToken은 DB나 Redis에 저장하는 로직 추가

        // 5. 프론트엔드로 JWT를 실어서 리다이렉트!
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/success") // 프론트의 콜백 페이지
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        log.info("JWT 발급 완료. 프론트엔드로 리다이렉트: {}", targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
