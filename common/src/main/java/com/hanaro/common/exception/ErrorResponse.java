package com.hanaro.common.exception;

public record ErrorResponse(
    String code,
    String message,
    Object data
) {
  public static ErrorResponse of(ErrorCode errorCode) {
    return new ErrorResponse(
        errorCode.getCode(),
        errorCode.getMessage(),
        null
    );
  }
}
