package com.project.cozystay.user.exception;

public class UserEmailAlreadyExistsException extends RuntimeException {

    public UserEmailAlreadyExistsException(String message) {
        super(message);
    }

    public static UserEmailAlreadyExistsException of(String email) {
        return new UserEmailAlreadyExistsException("이미 존재하는 이메일입니다. email=" + email);
    }
}
