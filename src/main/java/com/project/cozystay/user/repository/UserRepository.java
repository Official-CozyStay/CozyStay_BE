package com.project.cozystay.user.repository;

import com.project.cozystay.user.domain.AuthProvider;
import com.project.cozystay.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Provider ID로 회원 조회
    Optional<User> findByProviderId(String providerId);

    // Provider ID와 Provider 로 회원 조회
    Optional<User> findByProviderIdAndProvider(String providerId, AuthProvider provider);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}
