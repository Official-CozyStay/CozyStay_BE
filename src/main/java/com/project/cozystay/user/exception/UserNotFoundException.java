package com.project.cozystay.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("사용자가 존재하지 않습니다. ID: " + userId);
    }
}
