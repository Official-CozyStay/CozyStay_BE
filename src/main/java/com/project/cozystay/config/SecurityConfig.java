package com.project.cozystay.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.cozystay.auth.CustomOAuth2UserService;
import com.project.cozystay.auth.JwtAuthenticationFilter;
import com.project.cozystay.auth.OAuth2LoginSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpMethod;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // JWT 방식이므로 세션 STATELESS, CSRF/FormLogin/HttpBasic 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 예외 처리 - 리다이렉트 대신 JSON 401
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint())
                )

                // URL별 권한 설정
                .authorizeHttpRequests(authz -> authz
                        // Swagger UI 개발 편의 기능 모두 허용
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // "/", "/api/auth/**", "/login/oauth2/**" (로그인 관련 경로)는 모두 허용
                        .requestMatchers("/", "/auth/success", "/login/**", "/oauth2/**", "/api/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/review/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/users/*/public-profile").permitAll()

                        .requestMatchers("/ws/**").permitAll()  // WebSocket SockJS 핸드셰이크 허용

                        .requestMatchers("/api/booking-guests/invitations/**").permitAll() // 비회원 초대 링크 로그인 없이 접근

                        // 숙소 검색 관련 API 모두 허용
                        .requestMatchers(HttpMethod.GET, "/api/accommodations/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/search/**").permitAll()
                        .requestMatchers("/api/test/es-sync").permitAll()

                        .requestMatchers("/error").permitAll() // TODO: 원인 로그를 바로 볼 수 있게 임시적으로
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2

                        // .../oauth2/authorization/{...}로 오는 요청들 처리
                        .authorizationEndpoint(ep -> ep
                                .authorizationRequestRepository(
                                        new HttpSessionOAuth2AuthorizationRequestRepository()
                                )
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. 허용할 프론트엔드 Origin
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        // 배포 후에는 "https://cozystay-frontend.com" 이런 거 추가

        // 2. 허용 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 3. 허용 헤더 (Authorization 포함)
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));

        // 4. 인증정보(쿠키/Authorization 헤더) 포함 허용
        config.setAllowCredentials(true);

        // 5. Preflight 캐시 시간(초)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 대해 이 CORS 설정을 적용
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (HttpServletRequest request,
                HttpServletResponse response,
                org.springframework.security.core.AuthenticationException authException) -> {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json;charset=UTF-8");

            Map<String, Object> body = new HashMap<>();
            body.put("success", false);
            body.put("message", "인증이 필요합니다.");
            body.put("path", request.getRequestURI());

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(), body);
        };
    }
}
