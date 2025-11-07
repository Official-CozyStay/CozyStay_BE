package com.project.cozystay.config;

import com.project.cozystay.auth.CustomOAuth2UserService;
import com.project.cozystay.auth.JwtAuthenticationFilter;
import com.project.cozystay.auth.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // 1. JWT 방식이므로 세션 STATELESS, CSRF/FormLogin/HttpBasic 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 2. URL별 권한 설정 (이게 바로 카카오 창이 안 뜨게 하는 부분)
                .authorizeHttpRequests(authz -> authz
                        // Swagger UI, H2 콘솔 등 개발 편의 기능 모두 허용
                        .requestMatchers("/swagger-ui.html", "/v3/api-docs/**", "/h2-console/**").permitAll()

                        // ⭐️ "/" (정문), "/api/auth/**", "/login/oauth2/**" (로그인 관련 경로)는 모두 허용
                        .requestMatchers("/", "/auth/success", "/login/**", "/oauth2/**").permitAll()

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 3. ✨ OAuth2 로그인 설정 ✨
                .oauth2Login(oauth2 -> oauth2

                        // .../oauth2/authorization/{...}로 오는 요청들 처리
                        .authorizationEndpoint(ep -> ep
                                .authorizationRequestRepository(
                                        new HttpSessionOAuth2AuthorizationRequestRepository()
                                )
                        )
                        // (중요) 카카오가 정보 줬을 때(7단계) 실행할 서비스 등록
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // (중요) 로그인 최종 성공 시(8단계) 실행할 핸들러 등록
                        .successHandler(oAuth2LoginSuccessHandler)
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
