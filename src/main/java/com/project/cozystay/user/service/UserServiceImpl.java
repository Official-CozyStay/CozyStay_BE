package com.project.cozystay.user.service;

import com.project.cozystay.user.domain.AuthProvider;
import com.project.cozystay.user.domain.Role;
import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.domain.UserGrade;
import com.project.cozystay.user.dto.PublicUserProfileResponse;
import com.project.cozystay.user.dto.SignUpRequest;
import com.project.cozystay.user.dto.UserGradeResponse;
import com.project.cozystay.user.dto.UserProfileResponse;
import com.project.cozystay.user.dto.UserProfileUpdateRequest;
import com.project.cozystay.user.exception.UserEmailAlreadyExistsException;
import com.project.cozystay.user.exception.UserNameAlreadyExistsException;
import com.project.cozystay.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signUp(SignUpRequest signUpRequest) {
        if (userRepository.findByUsername(signUpRequest.username()).isPresent()) {
            throw UserNameAlreadyExistsException.of(signUpRequest.username());
        }
        if (signUpRequest.email() != null && userRepository.findByEmail(signUpRequest.email()).isPresent()) {
            throw UserEmailAlreadyExistsException.of(signUpRequest.email());
        }

        User user = User.builder()
                .username(signUpRequest.username())
                .email(signUpRequest.email())
                .password(passwordEncoder.encode(signUpRequest.password()))
                .nickName(signUpRequest.nickName())
                .userRole(Role.USER)
                .provider(AuthProvider.LOCAL)
                .providerId(signUpRequest.username()) // providerId를 username으로 사용
                .userGrade(UserGrade.BRONZE)
                .isEmailVerified(false) // 이메일 인증 여부 초기화
                .build();

        userRepository.save(user);
    }

    /* 등급 기준 예시 (횟수/박수 기준) */
    private static final int SILVER_BOOKING_THRESHOLD = 5;
    private static final int SILVER_NIGHTS_THRESHOLD = 10;

    private static final int GOLD_BOOKING_THRESHOLD = 10;
    private static final int GOLD_NIGHTS_THRESHOLD = 20;

    private static final int PLATINUM_BOOKING_THRESHOLD = 20;
    private static final int PLATINUM_NIGHTS_THRESHOLD = 40;

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


    // ======= 내부 공통 메서드 =======

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
     * 내부에서만 쓰는 작은 값 객체
     */
    private record GradeInfo(
            String nextGradeName,
            int remainingBookings,
            int remainingNights
    ) {
    }
}
