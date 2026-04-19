package com.hanaro.common.exception;

import lombok.Getter;

@Getter
public enum GlobalErrorCode implements ErrorCode {

  INTERNAL_SERVER_ERROR("G001", "서버 오류", 500),
  INVALID_INPUT("G002", "잘못된 요청", 400);


  private final String code;
  private final String message;
  private final int status;

  GlobalErrorCode(String code, String message, int status) {
    this.code = code;
    this.message = message;
    this.status = status;
  }
}