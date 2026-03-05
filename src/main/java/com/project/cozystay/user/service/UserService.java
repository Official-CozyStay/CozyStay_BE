package com.project.cozystay.user.service;

import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.dto.*;

import java.util.List;
import java.util.Map;

public interface UserService {

    void signUp(SignUpRequest signUpRequest);

    void existsByUsername(String username);

    SignInResponse signIn(SignInRequest signInRequest);

    UserProfileResponse getMyProfile(User user);

    UserGradeResponse getMyGrade(Long userId);

    UserProfileResponse updateMyProfile(Long userId, UserProfileUpdateRequest request);

    void becomeHost(Long userId);

    PublicUserProfileResponse getPublicProfile(Long userId);

    Map<Long, User> getUsers(List<Long> userIds);

}
