package com.project.cozystay.user.service;

import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.dto.PublicUserProfileResponse;
import com.project.cozystay.user.dto.SignUpRequest;
import com.project.cozystay.user.dto.UserGradeResponse;
import com.project.cozystay.user.dto.UserProfileResponse;
import com.project.cozystay.user.dto.UserProfileUpdateRequest;

public interface UserService {

    void signUp(SignUpRequest signUpRequest);

    UserProfileResponse getMyProfile(User user);

    UserGradeResponse getMyGrade(Long userId);

    UserProfileResponse updateMyProfile(Long userId, UserProfileUpdateRequest request);

    void becomeHost(Long userId);

    PublicUserProfileResponse getPublicProfile(Long userId);

}
