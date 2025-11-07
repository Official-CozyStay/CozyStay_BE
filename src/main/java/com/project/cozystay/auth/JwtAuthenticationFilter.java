package com.project.cozystay.auth;

import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository; // 🚨 토큰에서 ID 꺼낸 후 DB에서 실제 유저 조회

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // ⭐️ "startsWith" 대신 "equals" 또는 "antMatcher"를 사용해야 합니다.
        // ⭐️ 이 목록은 SecurityConfig의 permitAll()과 "정확히" 일치해야 합니다.
        List<String> excludePath = List.of(
                "/",
                "/auth/success",
                "/login" // /login?error 등도 처리
        );

        String path = request.getRequestURI();

        // ⭐️ 정확히 일치하는 경로(excludePath)가 있으면 true (필터링 안 함)
        if (excludePath.contains(path)) {
            return true;
        }

        // ⭐️ /swagger-ui, /v3/api-docs, /h2-console 등 "하위 경로"를 모두 열어야 할 때
        if (path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/h2-console") ||
                path.startsWith("/login/oauth2")) { // /login/oauth2/code/kakao
            return true;
        }

        return false; // 그 외 모든 경로는 필터링 함
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청 헤더에서 "Authorization" 헤더를 찾습니다.
        String token = resolveToken(request);

        // 2. 토큰이 존재하고, 유효한 토큰인지 검사합니다. (JwtProvider 활용)
        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
            try {
                // 3. 토큰에서 사용자 ID (우리 DB의 PK)를 꺼냅니다.
                Long userId = jwtProvider.getUserId(token);

                // 4. 🚨 DB에서 실제 사용자를 조회합니다. (토큰이 위조되지 않았어도, 탈퇴한 회원일 수 있으므로)
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("해당 ID의 회원을 찾을 수 없습니다.")); // TODO: 커스텀 예외

                // 5. ✨ 스프링 시큐리티 컨텍스트에 "인증된 사용자" 정보를 등록합니다.
                //    이게 있어야 @AuthenticationPrincipal 같은 어노테이션이 동작하고,
                //    Controller에서 "누가" 요청했는지 알 수 있습니다.
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, // Principal (인증된 주체, 여기서는 User 객체 자체를 넣음)
                        null, // Credentials (자격 증명, JWT 방식에선 불필요)
                        Collections.singleton(new SimpleGrantedAuthority(user.getUserRole().getKey())) // 권한
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", user.getId(), request.getRequestURI());

            } catch (Exception e) {
                log.warn("토큰에서 인증 정보를 가져오는 데 실패했습니다.", e);
                // (선택) 여기서 response.sendError()로 401 응답을 즉시 보낼 수도 있습니다.
            }
        } else {
            // 토큰이 없거나 유효하지 않지만, 로그인이 필요 없는 페이지(permitAll)일 수 있으므로 일단 통과
            log.trace("유효한 JWT 토큰이 없습니다, uri: {}", request.getRequestURI());
        }

        // 6. 다음 필터로 요청을 넘깁니다.
        filterChain.doFilter(request, response);
    }

    /**
     * Request Header에서 "Authorization" 토큰을 꺼내는 헬퍼 메서드
     * "Bearer [토큰값]" 형태여야 합니다.
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 7글자 뒤부터가 실제 토큰
        }
        return null;
    }
}
