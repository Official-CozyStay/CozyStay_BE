package com.project.cozystay.user.service;

import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.dto.*;

public interface UserService {

    void signUp(SignUpRequest signUpRequest);

    SignInResponse signIn(SignInRequest signInRequest);

    UserProfileResponse getMyProfile(User user);

    UserGradeResponse getMyGrade(Long userId);

    UserProfileResponse updateMyProfile(Long userId, UserProfileUpdateRequest request);

    void becomeHost(Long userId);

    PublicUserProfileResponse getPublicProfile(Long userId);

}
