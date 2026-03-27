package com.project.cozystay.comment.dto;

import com.project.cozystay.comment.domain.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponseDTO {

    private Long commentId;
    private String content;
    private Long authorId;
    private String authorNickname;
    private String authorProfileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponseDTO from(Comment comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getAuthor().getId())
                .authorNickname(comment.getAuthor().getNickName())
                .authorProfileImage(comment.getAuthor().getProfileImageUrl())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
