package com.hanaro.userservice.exception;

import com.hanaro.common.exception.BaseException;
import com.hanaro.common.exception.ErrorCode;

public class UserException extends BaseException {
  public UserException(ErrorCode errorCode) {
    super(errorCode);
  }
}
