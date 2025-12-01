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
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Value("${redirect.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();

        Long providerId = principal.getAttribute("id");
        User user = Optional.ofNullable(principal.getUser())
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        log.info("로그인 성공!. Provider ID: {}", providerId);

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        // TODO: refreshToken은 DB나 Redis에 저장하는 로직 추가

        // TODO: URL에 JWT 토큰 넘기는 방식 변경
        // TODO: 같은 도메인이라면 HttpOnly 쿠키 사용
        // TODO: 다른 도메인이라면 짧은 인증 코드(UUID) 방식
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl) // 프론트의 콜백 페이지
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        log.info("JWT 발급 완료. 프론트엔드로 리다이렉트: {}", targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
