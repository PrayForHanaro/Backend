package com.hanaro.common.exception;


public interface ErrorCode {
  String getCode();
  String getMessage();
  int getStatus();
}
