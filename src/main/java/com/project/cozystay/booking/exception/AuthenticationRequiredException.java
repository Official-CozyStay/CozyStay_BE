package com.project.cozystay.booking.exception;

public class AuthenticationRequiredException extends RuntimeException{
    public AuthenticationRequiredException(){
        super("인증이 필요합니다.");
    }
}
