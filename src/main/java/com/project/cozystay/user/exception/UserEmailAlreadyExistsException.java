package com.project.cozystay.user.exception;

public class UserEmailAlreadyExistsException extends RuntimeException {

    public UserEmailAlreadyExistsException() {
        super("이미 존재하는 이메일입니다.");
    }
}
