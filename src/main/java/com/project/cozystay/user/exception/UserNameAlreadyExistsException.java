package com.project.cozystay.user.exception;

public class UserNameAlreadyExistsException extends RuntimeException {

    public UserNameAlreadyExistsException(String message) {
        super(message);
    }

    public static UserNameAlreadyExistsException of(String username) {
        return new UserNameAlreadyExistsException("이미 사용 중인 아이디입니다.");
    }
}
