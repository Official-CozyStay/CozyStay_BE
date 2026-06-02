package com.project.cozystay.user.exception;

public class UserNameAlreadyExistsException extends RuntimeException {

    public UserNameAlreadyExistsException() {
        super("이미 사용 중인 아이디입니다.");
    }

}
