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
    private final UserRepository userRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        List<String> excludePath = List.of(
                "/",
                "/auth/success",
                "/login" // /login?error 등도 처리
        );

        String path = request.getRequestURI();

        if (excludePath.contains(path)) {
            return true;
        }

        if (path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/h2-console") ||
                path.startsWith("/login/oauth2") ||
                path.startsWith("/oauth2/authorization")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {


        String token = resolveToken(request);


        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
            try {
                Long userId = jwtProvider.getUserId(token);

                User user = userRepository.findById(userId)
                        // TODO: 커스텀 예외
                        .orElseThrow(() -> new RuntimeException("해당 ID의 회원을 찾을 수 없습니다."));

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, // Principal (인증된 주체, 여기서는 User 객체 자체를 넣음)
                        null, // Credentials (자격 증명, JWT 방식에선 불필요)
                        Collections.singleton(new SimpleGrantedAuthority(user.getUserRole().getKey())) // 권한
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", user.getId(), request.getRequestURI());

            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                log.warn("만료된 JWT 토큰입니다. token={}, message={}", token, e.getMessage());
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                log.warn("형식이 잘못된 JWT 토큰입니다. token={}, message={}", token, e.getMessage());
            } catch (io.jsonwebtoken.JwtException e) {
                log.warn("JWT 관련 예외가 발생했습니다. token={}, message={}", token, e.getMessage());
            } catch (Exception e) {
                log.warn("토큰에서 인증 정보를 가져오는 데 실패했습니다.", e);
            }
        }
        else {
            log.trace("유효한 JWT 토큰이 없습니다, uri: {}", request.getRequestURI());
        }

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
