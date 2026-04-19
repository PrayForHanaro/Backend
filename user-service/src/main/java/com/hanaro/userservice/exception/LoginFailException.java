package com.hanaro.userservice.exception;

public class LoginFailException extends UserException {
    public LoginFailException() {
        super(UserErrorCode.LOGIN_FAIL);
    }
}