package com.project.cozystay.comment.dto;

import com.project.cozystay.comment.domain.ReviewType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record CommentRequestDTO(
    @NotNull(message = "리뷰 ID는 비워둘 수 없습니다.")
    Long reviewId,
    @NotNull(message = "리뷰 타입은 비워둘 수 없습니다.")
    ReviewType reviewType,
    @NotBlank(message = "내용은 비워둘 수 없습니다.")
    String content
) {
}