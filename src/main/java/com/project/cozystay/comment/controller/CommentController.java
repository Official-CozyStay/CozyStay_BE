package com.project.cozystay.comment.controller;

import com.project.cozystay.auth.CustomOAuth2User;
import com.project.cozystay.comment.dto.CommentRequestDTO;
import com.project.cozystay.comment.dto.CommentResponseDTO;
import com.project.cozystay.comment.dto.CommentUpdateRequestDTO;
import com.project.cozystay.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment", description = "댓글(답글) 관련 API")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성
     */
    @Operation(summary = "댓글 생성", description = "리뷰나 다른 댓글에 대한 답글을 생성합니다.")
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(
            @RequestBody @Valid CommentRequestDTO commentRequestDto,
            @AuthenticationPrincipal CustomOAuth2User customUser
    ) {
        Long userId = customUser.getId();
        CommentResponseDTO responseDto = commentService.createComment(userId, commentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 댓글 단건 조회
     */
    @Operation(summary = "댓글 상세 조회", description = "특정 댓글의 상세 정보를 조회합니다.")
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> getComment(
            @PathVariable Long commentId
    ) {
        CommentResponseDTO responseDto = commentService.findComment(commentId);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 모든 댓글 페이징 조회
     */
    @Operation(summary = "전체 댓글 목록 조회", description = "모든 댓글 목록을 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<CommentResponseDTO>> getAllComments(Pageable pageable) {
        Page<CommentResponseDTO> responseDtos = commentService.findAllComments(pageable);
        return ResponseEntity.ok(responseDtos);
    }

    /**
     * 댓글 수정
     */
    @Operation(summary = "댓글 수정", description = "작성한 댓글의 내용을 수정합니다.")
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequestDTO commentUpdateRequestDTO,
            @AuthenticationPrincipal CustomOAuth2User customUser
    ) {

        Long userId = customUser.getId();
        CommentResponseDTO responseDTO = commentService.updateComment(userId, commentId, commentUpdateRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * 댓글 삭제
     */
    @Operation(summary = "댓글 삭제", description = "작성한 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomOAuth2User customUser
    ) {

        Long userId = customUser.getId();
        commentService.deleteComment(userId, commentId);
        return ResponseEntity.noContent().build();
    }
}
