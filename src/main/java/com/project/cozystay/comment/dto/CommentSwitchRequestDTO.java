package com.project.cozystay.comment.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentSwitchRequestDTO(
        @NotBlank(message = "내용은 비워둘 수 없습니다.")
        String content
) {
}
