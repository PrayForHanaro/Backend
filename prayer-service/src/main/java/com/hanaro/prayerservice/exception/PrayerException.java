package com.hanaro.prayerservice.exception;

import com.hanaro.common.exception.BaseException;
import com.hanaro.common.exception.ErrorCode;

public class PrayerException extends BaseException {

    public PrayerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
