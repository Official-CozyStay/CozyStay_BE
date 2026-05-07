package com.project.cozystay.review.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(String message) {
        super(message);
    }

    public ReviewNotFoundException(Long reviewId) {
        super("해당 리뷰가 존재하지 않습니다. ID: " + reviewId);
    }
}
