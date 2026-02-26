package com.project.cozystay.user.service;

import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.dto.PublicUserProfileResponse;
import com.project.cozystay.user.dto.UserGradeResponse;
import com.project.cozystay.user.dto.UserProfileResponse;
import com.project.cozystay.user.dto.UserProfileUpdateRequest;

import java.util.List;
import java.util.Map;

public interface UserService {

    UserProfileResponse getMyProfile(User user);

    UserGradeResponse getMyGrade(Long userId);

    UserProfileResponse updateMyProfile(Long userId, UserProfileUpdateRequest request);

    void becomeHost(Long userId);

    PublicUserProfileResponse getPublicProfile(Long userId);

    Map<Long, User> getUsers(List<Long> userIds);

    User getUserRefOrThrow(Long userId);

}
