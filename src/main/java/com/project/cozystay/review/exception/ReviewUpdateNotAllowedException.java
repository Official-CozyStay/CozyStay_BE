package com.project.cozystay.review.exception;

// 409 Conflict
public class ReviewUpdateNotAllowedException extends RuntimeException {
    public ReviewUpdateNotAllowedException(String message) {
        super(message);
    }
}
