package com.project.cozystay.user.service;

import com.project.cozystay.user.dto.PublicUserProfileResponse;
import com.project.cozystay.user.dto.UserGradeResponse;
import com.project.cozystay.user.dto.UserProfileResponse;
import com.project.cozystay.user.dto.UserProfileUpdateRequest;

public interface UserService {

    UserProfileResponse getMyProfile(Long userId);

    UserGradeResponse getMyGrade(Long userId);

    UserProfileResponse updateMyProfile(Long userId, UserProfileUpdateRequest request);

    void becomeHost(Long userId);

    PublicUserProfileResponse getPublicProfile(Long userId);

}
