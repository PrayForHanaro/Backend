package com.hanaro.userservice.exception;

import com.hanaro.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum UserErrorCode implements ErrorCode {
  USER_NOT_FOUND("U001", "유저를 찾을 수 없습니다", 404),
  DUPLICATE_USER("U002", "중복 유저", 400),
  INSUFFICIENT_BALANCE("U003", "잔액이 부족합니다", 400);

  private final String code;
  private final String message;
  private final int status;

  UserErrorCode(String code, String message, int status) {
    this.code = code;
    this.message = message;
    this.status = status;
  }
}
