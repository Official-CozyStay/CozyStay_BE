package com.project.cozystay.review.exception;

public class ReviewCreationNotAllowedException extends RuntimeException {
    public ReviewCreationNotAllowedException(String message) {
        super(message);
    }
}
