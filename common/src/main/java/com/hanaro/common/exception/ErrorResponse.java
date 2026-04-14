package com.hanaro.common.exception;

public record ErrorResponse(
    String code,
    String message,
    int status
) {
  public static ErrorResponse of(ErrorCode errorCode) {
    return new ErrorResponse(
        errorCode.getCode(),
        errorCode.getMessage(),
        errorCode.getStatus()
    );
  }
}
