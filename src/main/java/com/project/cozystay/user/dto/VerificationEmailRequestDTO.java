package com.project.cozystay.user.dto;

/**
 * 이메일 코드 검증 요청 DTO
 */
public record VerificationEmailRequestDTO(
        String email,
        String code
) {
}
