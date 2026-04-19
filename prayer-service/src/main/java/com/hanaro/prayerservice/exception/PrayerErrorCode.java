package com.hanaro.prayerservice.exception;

import com.hanaro.common.exception.ErrorCode;
import lombok.Getter;

/**
 * prayer-service 에러 코드.
 * 프리픽스 P + 3자리. 도메인별 100단위 밴드 (ADR-006 예정).
 *   P0xx: Gift (정기 송금 설정)
 *   P1xx: PrayerSavings (기도 메시지)
 *   P2xx: OnceTransfer (일회성 축복)
 *   P3xx: Savings (적금 가입)
 */
@Getter
public enum PrayerErrorCode implements ErrorCode {

    GIFT_NOT_FOUND("P001", "기도적금을 찾을 수 없습니다", 404),
    GIFT_NOT_OWNED("P002", "기도적금 접근 권한이 없습니다", 403),
    DUPLICATE_GIFT("P003", "이미 해당 대상자에게 가입된 기도적금이 있습니다", 409),
    NO_DEFAULT_ACCOUNT("P005", "기본 출금 계좌를 먼저 등록해주세요", 400),

    MESSAGE_NOT_FOUND("P101", "기도 메시지를 찾을 수 없습니다", 404),
    MESSAGE_NOT_OWNED("P102", "메시지 수정 권한이 없습니다", 403),
    MESSAGE_CONTENT_INVALID("P103", "메시지 내용이 유효하지 않습니다", 400),

    ONCE_AMOUNT_INVALID("P201", "축복 금액이 유효하지 않습니다", 400),
    TRANSFER_FAILED("P202", "송금 처리 중 오류가 발생했습니다", 500),

    SAVINGS_PRODUCT_NOT_FOUND("P301", "적금 상품을 찾을 수 없습니다", 404),
    ADMIN_ONLY("P302", "관리자 권한이 필요합니다", 403);

    private final String code;
    private final String message;
    private final int status;

    PrayerErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
