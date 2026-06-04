package com.project.cozystay.user.service;

import com.project.cozystay.auth.CustomUserDetails;
import com.project.cozystay.auth.JwtProvider;
import com.project.cozystay.user.domain.AuthProvider;
import com.project.cozystay.user.domain.Role;
import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.domain.UserGrade;
import com.project.cozystay.user.dto.*;
import com.project.cozystay.user.exception.UserEmailAlreadyExistsException;
import com.project.cozystay.user.exception.UserNameAlreadyExistsException;
import com.project.cozystay.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public void signUp(SignUpRequest signUpRequest) {

        if (userRepository.findByUsername(signUpRequest.username()).isPresent()) {
            throw new UserNameAlreadyExistsException();
        }

        if (userRepository.existsByEmail(signUpRequest.email())) {
            throw new UserEmailAlreadyExistsException();
        }

        User user = User.builder()
                .username(signUpRequest.username())
                .password(passwordEncoder.encode(signUpRequest.password()))
                .email(signUpRequest.email())
                .nickName(signUpRequest.nickName())
                .userRole(Role.USER)
                .provider(AuthProvider.LOCAL)
                .providerId(signUpRequest.username()) // providerId를 username으로 사용
                .userGrade(UserGrade.BRONZE)
                .isEmailVerified(true)
                .build();

        userRepository.save(user);
    }

    @Override
    public SignInResponse signIn(SignInRequest signInRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInRequest.username(), signInRequest.password())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserRole());
            String refreshToken = jwtProvider.createRefreshToken(user.getId());

            return SignInResponse.builder()
                    .userId(user.getId())
                    .nickName(user.getNickName())
                    .role(user.getUserRole().name())
                    .grantType("Bearer")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("ID 또는 비밀번호가 틀립니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void existsByUsername(String userName){

        if (userRepository.findByUsername(userName).isPresent()) {
            throw new UserNameAlreadyExistsException();
        }

    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(User user) {
        return UserProfileResponse.from(user);
    }

    @Override
    public UserProfileResponse updateMyProfile(Long userId, UserProfileUpdateRequest request) {
        User user = getUserOrThrow(userId);

        user.updateNicknameAndProfile(request.nickName(), request.profileImageUrl());

        // JPA 변경 감지로 자동 flush
        return UserProfileResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserGradeResponse getMyGrade(Long userId) {
        User user = getUserOrThrow(userId);

        GradeInfo nextGradeInfo = calculateNextGradeInfo(user);

        return UserGradeResponse.from(
                user,
                nextGradeInfo.nextGradeName(),
                nextGradeInfo.remainingBookings(),
                nextGradeInfo.remainingNights()
        );
    }

    @Override
    public void becomeHost(Long userId) {
        User user = getUserOrThrow(userId);

        // 이미 HOST/ADMIN이면 예외
        if (user.getUserRole() == Role.HOST || user.getUserRole() == Role.ADMIN) {
            throw new IllegalStateException("이미 호스트 권한을 가지고 있습니다.");
        }

        // TODO: 여기서 호스트 전환에 필요한 추가 정보(전화번호, 계좌 등) 검증 로직 넣어도 됨
        user.changeRole(Role.HOST);
    }

    @Override
    @Transactional(readOnly = true)
    public PublicUserProfileResponse getPublicProfile(Long userId) {
        User user = getUserOrThrow(userId);

        return PublicUserProfileResponse.from(user);
    }

    /**
     * 연관관계를 맺고 있는 곳에서 프록시 객체만 얻기 위해 사용 (불필요한 조회 방지)
     *
     */
    @Override
    public User getUserRefOrThrow(Long userId){

        return userRepository.getReferenceById(userId);
    }


    // ======= 내부 공통 메서드 ======
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));
    }

    /**
     * 다음 등급과, 그 등급까지 필요한 예약 수/박 수 계산
     * - 단순 로직 예시, 나중에 바뀌어도 여기만 손대면 됨
     */
    private GradeInfo calculateNextGradeInfo(User user) {
        UserGrade currentGrade = user.getUserGrade();
        UserGrade nextGrade = currentGrade.getNextGrade();

        // 최고 등급인 경우 (다음 등급이 없음)
        if (nextGrade == null) {
            return new GradeInfo("PLATINUM", 0, 0);
        }

        // 다음 등급까지 남은 실적 계산
        int remainingBookings = nextGrade.calculateRemainingBookings(user.getTotalCompletedBookings());
        int remainingNights = nextGrade.calculateRemainingNights(user.getTotalStayedNights());

        return new GradeInfo(nextGrade.name(), remainingBookings, remainingNights);
    }

    /**
     * 유저 ID 리스트를 받아서 Map<Long, User> 형태로 반환
     */
    @Override
    public Map<Long, User> getUsers(List<Long> userIds){
        List<User> users = userRepository.findAllById(userIds);

        return users.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> user
                ));
    }
    /**
     * 내부에서만 쓰는 작은 값 객체
     */
    private record GradeInfo(
            String nextGradeName,
            int remainingBookings,
            int remainingNights
    ) {
    }
}
