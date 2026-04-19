package com.hanaro.offeringservice.exception;

import com.hanaro.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum OfferingErrorCode implements ErrorCode {
    OFFERING_NOT_FOUND("OF001", "헌금 정보를 찾을 수 없습니다", 404),
    WITHDRAWAL_FAILED("OF002", "계좌 출금에 실패했습니다", 500),
    POINT_USE_FAILED("OF003", "포인트 차감에 실패했습니다", 500),
    OFFERING_EVENT_PUBLISH_FAILED("OF004", "헌금 이벤트 발행에 실패했습니다", 500);

    private final String code;
    private final String message;
    private final int status;

    OfferingErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}