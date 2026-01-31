package com.project.cozystay.comment.service;

import com.project.cozystay.comment.domain.Comment;
import com.project.cozystay.comment.domain.ReviewType;
import com.project.cozystay.comment.dto.CommentRequestDTO;
import com.project.cozystay.comment.dto.CommentResponseDTO;
import com.project.cozystay.comment.dto.CommentUpdateRequestDTO;
import com.project.cozystay.comment.exception.CommentNotFoundException;
import com.project.cozystay.comment.repository.CommentRepository;
import com.project.cozystay.review.domain.Commentable;
import com.project.cozystay.review.repository.AccommodationReviewRepository;
import com.project.cozystay.review.repository.UserReviewRepository;
import com.project.cozystay.user.domain.User;
import com.project.cozystay.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final UserReviewRepository userReviewRepository;
    private final AccommodationReviewRepository accommodationReviewRepository;
    private final Map<ReviewType, Function<Long, Commentable>> reviewFetcherMap;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository,
                          UserReviewRepository userReviewRepository, AccommodationReviewRepository accommodationReviewRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.userReviewRepository = userReviewRepository;
        this.accommodationReviewRepository = accommodationReviewRepository;
        this.reviewFetcherMap = new EnumMap<>(ReviewType.class);
        initializeFetcherMap();
    }

    private void initializeFetcherMap() {
        reviewFetcherMap.put(ReviewType.USER, id -> userReviewRepository.findById(id)
                .map(Commentable.class::cast)
                .orElseThrow(() -> new EntityNotFoundException("사용자 리뷰를 찾을 수 없습니다.")));
        reviewFetcherMap.put(ReviewType.ACCOMMODATION, id -> accommodationReviewRepository.findById(id)
                .map(Commentable.class::cast)
                .orElseThrow(() -> new EntityNotFoundException("숙소 리뷰를 찾을 수 없습니다.")));
    }

    @Transactional
    public CommentResponseDTO createComment(Long userId, CommentRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Comment comment = Comment.of(user, requestDTO.content(), requestDTO.reviewType());
        Comment savedComment = commentRepository.save(comment);

        Function<Long, Commentable> fetcher = reviewFetcherMap.get(comment.getReviewType());
        if (fetcher == null) {
            throw new IllegalArgumentException("지원하지 않는 리뷰 타입입니다: " + requestDTO.reviewType());
        }

        Commentable review = fetcher.apply(requestDTO.reviewId());
        review.addComment(savedComment);

        return CommentResponseDTO.from(savedComment);
    }

    @Transactional(readOnly = true)
    public CommentResponseDTO findComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));
        return CommentResponseDTO.from(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDTO> findAllComments(Pageable pageable) {
        return commentRepository.findAll(pageable)
                .map(CommentResponseDTO::from);
    }

    @Transactional
    public CommentResponseDTO updateComment(Long userId, Long commentId, CommentUpdateRequestDTO commentRequestDto) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        comment.update(commentRequestDto.content());
        return CommentResponseDTO.from(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        switch (comment.getReviewType()) {
            case ACCOMMODATION ->
                accommodationReviewRepository.findByComment_CommentId(commentId)
                    .ifPresent(review -> review.addComment(null));
            case USER ->
                userReviewRepository.findByComment_CommentId(commentId)
                    .ifPresent(review -> review.addComment(null));
            default -> throw new EntityNotFoundException("지원하지 않는 리뷰 타입입니다: " + comment.getReviewType());
        }
    }
}