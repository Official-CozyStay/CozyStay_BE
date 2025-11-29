package com.project.cozystay.user.repository;

import com.project.cozystay.user.domain.AuthProvider;
import com.project.cozystay.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 카카오 ID로 회원 조회
    Optional<User> findByProviderId(String providerId);

    // DB PK ID로 회원 조회
    Optional<User> findUserById(Long userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
