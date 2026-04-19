package com.hanaro.userservice.exception;

public class SignUpFailException extends UserException {
    public SignUpFailException() {
        super(UserErrorCode.USER_ALREADY_EXIST);
    }
}